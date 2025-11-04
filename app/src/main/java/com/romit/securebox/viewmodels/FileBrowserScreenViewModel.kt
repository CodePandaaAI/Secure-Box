package com.romit.securebox.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.model.FileBrowserUiState
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.data.repository.FileRepository
import com.romit.securebox.util.StorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class FileBrowserScreenViewModel @Inject constructor(private val repository: FileRepository) :
    ViewModel() {
    private var _uiState = MutableStateFlow(FileBrowserUiState())
    val uiState = _uiState.asStateFlow()

    private var currentLoadJob: Job? = null

    fun getDirFiles(path: String) {
        currentLoadJob?.cancel()

        _uiState.update { it.copy(currPath = path, error = null, isLoading = true) }
        currentLoadJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val files = repository.getDirFileItems(path)
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(dirFiles = files, error = null, isLoading = false)
                    }
                }
                val dirWithSize = files.map { file ->
                    async {
                        if (file.isDirectory) {
                            val dirSize = StorageHelper.getDirectorySize(File(file.path))
                            file.copy(size = StorageHelper.formatSize(dirSize))
                        } else {
                            file
                        }
                    }
                }.awaitAll()

                // Step 3: Update UI with sizes
                // ✅ Check if still active before updating
                if (isActive) {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(dirFiles = dirWithSize, error = null, isLoading = false)
                        }
                    }
                }

            } catch (e: CancellationException) {

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
            }
        }
    }

    fun deleteFile(filePath: String) {
        viewModelScope.launch {
            repository.deleteFile(filePath).fold(
                onSuccess = { message ->
                    _uiState.update {
                        it.copy(successMessage = message, error = null)
                    }
                    getDirFiles(_uiState.value.currPath)
                },
                onFailure = { exception ->
                    // Use repository message if available, otherwise create custom
                    val errorMessage = exception.message ?: when (exception) {
                        is FileNotFoundException -> "File not found"
                        is SecurityException -> "Permission denied"
                        is IOException -> "Cannot delete file"
                        else -> "Failed to delete"
                    }

                    _uiState.update {
                        it.copy(error = errorMessage, successMessage = null)
                    }
                }
            )
        }
    }

    fun onRenamingFile(newName: String) {
        _uiState.update { it.copy(newFileName = newName) }
    }

    fun onRenameFileClicked() {
        viewModelScope.launch {
            val selectedFile = uiState.value.selectedFile ?: return@launch
            
            repository.renameFile(selectedFile.path, uiState.value.newFileName).fold(
                onSuccess = { message ->
                    _uiState.update {
                        it.copy(
                            successMessage = message,
                            error = null,
                            isRenameEnabled = false,  // ✅ Close dialog
                            newFileName = "",
                            selectedFile = null  // ✅ Clear selection
                        )
                    }
                    getDirFiles(_uiState.value.currPath)  // ✅ Refresh
                },
                onFailure = { exception ->
                    val errorMessage = exception.message ?: when (exception) {
                        is FileNotFoundException -> "File not found"
                        is IllegalArgumentException -> "Invalid name"
                        is FileAlreadyExistsException -> "Name already exists"
                        is IOException -> "Rename failed"
                        is SecurityException -> "Permission denied"
                        else -> "Unknown error"
                    }
                    _uiState.update {
                        it.copy(
                            error = errorMessage,
                            successMessage = null,
                            isRenameEnabled = false,
                            newFileName = "",
                            selectedFile = null
                        )
                    }
                }
            )
        }
    }

    fun toggleRenameDialog() {
        _uiState.update { it.copy(isRenameEnabled = !uiState.value.isRenameEnabled, newFileName = uiState.value.selectedFile?.name ?: "") }
    }

    fun toggleDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = !uiState.value.showDeleteDialog) }
    }

    fun selectedFileForBottomSheet(file: FileItem?) {
        _uiState.update { it.copy(selectedFile = file) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
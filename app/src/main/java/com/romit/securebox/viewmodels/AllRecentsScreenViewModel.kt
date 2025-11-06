package com.romit.securebox.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.model.AllRecentsUiState
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.data.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AllRecentsScreenViewModel @Inject constructor(private val repository: FileRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(AllRecentsUiState())
    val uiState = _uiState.asStateFlow()

    private var currentPage = 1
    private val pageSize = 20

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        if (uiState.value.isLoadingNextPage) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingNextPage = true) }

            try {
                val newFiles = repository.getRecentFiles(currentPage, pageSize)
                _uiState.update {
                    it.copy(
                        files = it.files + newFiles,
                        isLoadingNextPage = false
                    )
                }
                currentPage++
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoadingNextPage = false) }
            }
        }
    }

    fun deleteFile(filePath: String) {
        viewModelScope.launch {
            repository.deleteFile(filePath).fold(
                onSuccess = { message ->
                    _uiState.update {
                        it.copy(
                            successMessage = message,
                            error = null,
                            files = it.files.filterNot { fileItem -> fileItem.path == filePath }
                        )
                    }
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
            val newName = uiState.value.newFileName

            repository.renameFile(selectedFile.path, uiState.value.newFileName).fold(
                onSuccess = { message ->

                    // Re-calculate the new path
                    val newPath = File(selectedFile.path).parent!! + "/" + newName
                    // Create an updated copy of the file item
                    val updatedFile = selectedFile.copy(
                        name = newName,
                        path = newPath
                    )

                    _uiState.update {
                        it.copy(
                            successMessage = message,
                            error = null,
                            isRenameEnabled = false,  // ✅ Close dialog
                            newFileName = "",
                            selectedFile = null,  // ✅ Clear selection

                            files = it.files.map { fileInList ->
                                if (fileInList.path == selectedFile.path) {
                                    updatedFile
                                } else {
                                    fileInList
                                }
                            }
                        )
                    }
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
        _uiState.update {
            it.copy(
                isRenameEnabled = !uiState.value.isRenameEnabled,
                newFileName = uiState.value.selectedFile?.name ?: ""
            )
        }
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
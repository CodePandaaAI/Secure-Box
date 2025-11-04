package com.romit.securebox.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.data.model.HomeUiState
import com.romit.securebox.data.repository.FileRepository
import com.romit.securebox.util.StorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repository: FileRepository) :
    ViewModel() {
    private var _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getStorageCategories()
        getRecentFiles()
    }

    fun getRecentFiles() {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null, isLoading = true) }
            try {
                val recentFiles = repository.getRecentFiles(limit = 4)
                _uiState.update { it.copy(recentFiles = recentFiles, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun getStorageCategories() {
        viewModelScope.launch {
            try {
                val categories = StorageHelper.getStorageCategories()
                _uiState.update { it.copy(storageCategoriesList = categories) }

                val categoriesWithSizes = withContext(Dispatchers.IO) {
                    categories.map { dir ->
                        async {  // Each size calculated in parallel!
                            dir.copy(dirSize = repository.getDirectorySize(dir.path))
                        }
                    }.awaitAll()
                }
                _uiState.update { it.copy(storageCategoriesList = categoriesWithSizes) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
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
                    getRecentFiles()
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

            // ✅ Pass full path, not just name
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
                    getRecentFiles()
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
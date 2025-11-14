package com.romit.securebox.viewmodels

import android.os.Environment
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

    // ✅ Initialize HomeScreen data when ViewModel is created
    init {
        getStorageCategories()
        getRecentFiles()
    }

    // ========== HOME SCREEN FUNCTIONS (NEW) ==========

    fun getRecentFiles() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null, isRefreshing = true) }
            try {
                val recentFiles = repository.getRecentFiles(limit = 4)
                _uiState.update { it.copy(recentFiles = recentFiles, isRefreshing = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message, isRefreshing = false) }
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
                        async {
                            dir.copy(dirSize = repository.getDirectorySize(dir.path))
                        }
                    }.awaitAll()
                }
                _uiState.update { it.copy(storageCategoriesList = categoriesWithSizes) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    // ========== EXISTING FILE BROWSER FUNCTIONS ==========

    fun getDirFiles(path: String) {
        currentLoadJob?.cancel()

        _uiState.update { it.copy(browsingPath = path, errorMessage = null, isLoading = true) }
        currentLoadJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val files = repository.getDirFileItems(path)
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(browsingPathDirectories = files, errorMessage = null, isLoading = false)
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

                if (isActive) {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(browsingPathDirectories = dirWithSize, errorMessage = null, isLoading = false)
                        }
                    }
                }

            } catch (e: CancellationException) {
                // Ignore cancellation
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
                }
            }
        }
    }

    fun deleteFile(filePath: String) {
        viewModelScope.launch {
            repository.deleteFile(filePath).fold(
                onSuccess = { message ->
                    _uiState.update {
                        it.copy(successMessage = message, errorMessage = null)
                    }
                    // ✅ Refresh both browsing path and recent files
                    if (_uiState.value.browsingPath.isNotEmpty()) {
                        getDirFiles(_uiState.value.browsingPath)
                    }
                    getRecentFiles()
                },
                onFailure = { exception ->
                    val errorMessage = exception.message ?: when (exception) {
                        is FileNotFoundException -> "File not found"
                        is SecurityException -> "Permission denied"
                        is IOException -> "Cannot delete file"
                        else -> "Failed to delete"
                    }

                    _uiState.update {
                        it.copy(errorMessage = errorMessage, successMessage = null)
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
                            errorMessage = null,
                            showRenameInput = false,
                            newFileName = "",
                            selectedFile = null
                        )
                    }
                    // ✅ Refresh both browsing path and recent files
                    if (_uiState.value.browsingPath.isNotEmpty()) {
                        getDirFiles(_uiState.value.browsingPath)
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
                        else -> "Unknown errorMessage"
                    }
                    _uiState.update {
                        it.copy(
                            errorMessage = errorMessage,
                            successMessage = null,
                            showRenameInput = false,
                            newFileName = "",
                            selectedFile = null
                        )
                    }
                }
            )
        }
    }

    fun getDirs(dirPath: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null, successMessage = null, isLoading = true) }

            try {
                val files = repository.getDirs(path = dirPath)
                _uiState.update {
                    it.copy(
                        errorMessage = null,
                        successMessage = null,
                        isLoading = false,
                        operationTargetPathDirectories = files
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message, successMessage = null, isLoading = false) }
            }
        }
    }

    fun updateCurrentPath(newCurrPath: String) {
        _uiState.update { it.copy(operationTargetPath = newCurrPath) }
    }

    fun copyFile(filePath: String, destPath: String) {
        viewModelScope.launch {
            repository.copyFile(filePath, destPath).fold(
                onSuccess = { message ->
                    _uiState.update {
                        it.copy(
                            successMessage = message,
                            errorMessage = null
                        )
                    }
                    resetOperationState()
                },
                onFailure = { message ->
                    val error = message.message ?: when (message) {
                        is FileNotFoundException -> "File not found"
                        is FileAlreadyExistsException -> "File already exists"
                        is SecurityException -> "Permission denied"
                        is IOException -> "Copy failed"
                        else -> "Unknown errorMessage"
                    }
                    _uiState.update { it.copy(errorMessage = error, successMessage = null) }
                    resetOperationState()
                }
            )
        }
    }

    fun moveFile(filePath: String, destPath: String) {
        viewModelScope.launch {
            repository.moveTo(filePath, destPath).fold(
                onSuccess = { message ->
                    _uiState.update {
                        it.copy(
                            successMessage = message,
                            errorMessage = null
                        )
                    }
                    resetOperationState()
                },
                onFailure = { message ->
                    _uiState.update { it.copy(errorMessage = message.message, successMessage = null) }
                    resetOperationState()
                }
            )
        }
    }

    fun createFolder() {
        val currentPath = _uiState.value.operationTargetPath // ✅ Fixed: use operationTargetPath
        val folderName = _uiState.value.newFolderName.trim()

        if (folderName.isBlank()) {
            _uiState.update { it.copy(newFolderError = "Folder name cannot be empty") }
            return
        }

        viewModelScope.launch {
            repository.createFolder(currentPath, folderName).fold(
                onSuccess = { message ->
                    _uiState.update {
                        it.copy(
                            successMessage = message,
                            showCreateFolderDialog = false,
                            newFolderName = ""
                        )
                    }
                    getDirs(currentPath)
                },
                onFailure = { exception ->
                    val errorMessage = when (exception) {
                        is FileNotFoundException -> "Parent directory not found"
                        is SecurityException -> "Permission denied"
                        is IllegalArgumentException -> exception.message ?: "Invalid folder name"
                        is FileAlreadyExistsException -> "Folder already exists"
                        else -> "Failed to create folder: ${exception.message}"
                    }
                    _uiState.update { it.copy(newFolderError = errorMessage) }
                }
            )
        }
    }

    // ========== TOGGLE FUNCTIONS ==========

    fun toggleCreateFolderDialog() {
        _uiState.update {
            it.copy(
                showCreateFolderDialog = !it.showCreateFolderDialog,
                newFolderName = "",
                newFolderError = null
            )
        }
    }

    fun updateNewFolderName(name: String) {
        _uiState.update { it.copy(newFolderName = name, newFolderError = null) }
    }

    fun isCopyFile() {
        _uiState.update { it.copy(isCopyFile = true, isMoveFile = false) }
    }

    fun isMoveFile() {
        _uiState.update { it.copy(isCopyFile = false, isMoveFile = true) }
    }

    fun toggleRenameDialog() {
        _uiState.update {
            it.copy(
                showRenameInput = !uiState.value.showRenameInput,
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
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun resetOperationState() {
        _uiState.update {
            it.copy(
                operationTargetPath = Environment.getExternalStorageDirectory().absolutePath,
                operationTargetPathDirectories = emptyList(),
                isMoveFile = false,
                isCopyFile = false
            )
        }
    }
}
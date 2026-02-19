package com.romit.securebox.viewmodels

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.data.model.SharedFileOperationsUiState
import com.romit.securebox.data.repository.FileRepository
import com.romit.securebox.util.FileOperations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SharedFileOperationsViewModel @Inject constructor(val repository: FileRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(SharedFileOperationsUiState())
    val uiState = _uiState.asStateFlow()

    private val folderHistory = mutableListOf<String>()

    fun deleteFile(filePath: String) {
        viewModelScope.launch {
            repository.deleteFile(filePath).fold(
                onSuccess = { message ->
                    _uiState.update {
                        it.copy(successMessage = message, errorMessage = null)
                    }
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

    fun getDirs(dirPath: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    errorMessage = null,
                    successMessage = null,
                    isDestinationScreenLoading = true
                )
            }

            try {
                val files = repository.getDirs(path = dirPath)
                _uiState.update {
                    it.copy(
                        errorMessage = null,
                        successMessage = null,
                        isDestinationScreenLoading = false,
                        operationTargetPathDirectories = files
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = e.message,
                        successMessage = null,
                        isDestinationScreenLoading = false
                    )
                }
            }
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

                    clearAllOperationsState()
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

                    clearAllOperationsState()
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

                    clearAllOperationsState()
                },
                onFailure = { message ->
                    _uiState.update {
                        it.copy(
                            errorMessage = message.message,
                            successMessage = null
                        )
                    }

                    clearAllOperationsState()
                }
            )
        }
    }

    fun createFolder() {
        val folderName = _uiState.value.newFolderName.trim()

        if (folderName.isBlank()) {
            _uiState.update { it.copy(newFolderError = "Folder name cannot be empty") }
            return
        }

        viewModelScope.launch {
            repository.createFolder(uiState.value.operationTargetPath, folderName).fold(
                onSuccess = { message ->
                    _uiState.update {
                        it.copy(
                            successMessage = message,
                            showCreateFolderDialog = false,
                            newFolderName = ""
                        )
                    }
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

    fun toggleCreateFolderDialog() {
        _uiState.update {
            it.copy(
                showCreateFolderDialog = !uiState.value.showCreateFolderDialog,
                newFolderName = "",
                newFolderError = null
            )
        }
    }

    fun updateNewFolderName(name: String) {
        _uiState.update { it.copy(newFolderName = name, newFolderError = null) }
    }

    fun chooseOperation(fileOperation: FileOperations) {
        when (fileOperation) {
            FileOperations.NONE -> return

            FileOperations.COPY -> _uiState.update {
                it.copy(selectedOperation = fileOperation)
            }

            FileOperations.MOVE -> _uiState.update {
                it.copy(selectedOperation = fileOperation)
            }
        }
    }

    fun setOperationSourceFile(file: FileItem) {
        _uiState.update { it.copy(operationSourceFile = file) }
    }

    fun navigateToFolder(folderPath: String) {
        // ✅ Add current path to history before navigating
        val currentPath = _uiState.value.operationTargetPath
        if (currentPath.isNotEmpty() && folderHistory.lastOrNull() != currentPath) {
            folderHistory.add(currentPath)
        }

        updateCurrentPath(folderPath)
        getDirs(folderPath)
    }

    fun navigateBack(): Boolean {
        // ✅ Return true if we handled the back press, false if we should exit
        return if (folderHistory.isNotEmpty()) {
            val previousPath = folderHistory.removeAt(folderHistory.lastIndex)
            updateCurrentPath(previousPath)
            getDirs(previousPath)
            true
        } else {
            // We're at the root, let the system handle back press (exit DestinationScreen)
            false
        }
    }

    fun initializeDestinationScreen(startPath: String) {
        folderHistory.clear()
        updateCurrentPath(startPath)
        getDirs(startPath)
    }

    fun clearAllOperationsState() {
        _uiState.update {
            it.copy(
                selectedOperation = FileOperations.NONE,
                operationSourceFile = null,
                operationTargetPath = Environment.getExternalStorageDirectory().absolutePath,
                operationTargetPathDirectories = emptyList()
            )
        }
        // ✅ Clear folder history too
        folderHistory.clear()
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

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun selectedFileForBottomSheet(file: FileItem?) {
        _uiState.update { it.copy(selectedFile = file) }
    }

    fun updateCurrentPath(newCurrPath: String) {
        _uiState.update { it.copy(operationTargetPath = newCurrPath) }
    }
}
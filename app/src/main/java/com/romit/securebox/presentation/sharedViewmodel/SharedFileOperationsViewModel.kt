package com.romit.securebox.presentation.sharedViewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.domain.usecases.CopyFileUseCase
import com.romit.securebox.domain.usecases.CreateFolderUseCase
import com.romit.securebox.domain.usecases.DeleteFileUseCase
import com.romit.securebox.domain.usecases.GetDirectoriesUseCase
import com.romit.securebox.domain.usecases.MoveFileUseCase
import com.romit.securebox.domain.usecases.RenameFileUseCase
import com.romit.securebox.util.FileOperations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedFileOperationsViewModel @Inject constructor(
    private val deleteFileUseCase: DeleteFileUseCase,
    private val getDirectoriesUseCase: GetDirectoriesUseCase,
    private val renameFileUseCase: RenameFileUseCase,
    private val copyFileUseCase: CopyFileUseCase,
    private val createFolderUseCase: CreateFolderUseCase,
    private val moveFileUseCase: MoveFileUseCase
) :
    ViewModel() {
    private val _uiState = MutableStateFlow(SharedFileOperationsUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<SharedFileOperationsUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val folderHistory = mutableListOf<String>()

    fun deleteFile(filePath: String) {
        viewModelScope.launch {
            deleteFileUseCase(filePath).fold(
                onSuccess = { message ->
                    _uiState.update {
                        it.copy(successMessage = message, errorMessage = null)
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            errorMessage = exception.message ?: "Failed to delete",
                            successMessage = null
                        )
                    }
                }
            )
        }
    }

    fun getDirectories(dirPath: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    errorMessage = null,
                    successMessage = null,
                    isDestinationScreenLoading = true
                )
            }
            getDirectoriesUseCase(dirPath).fold(
                onSuccess = { files ->
                    _uiState.update {
                        it.copy(
                            errorMessage = null,
                            successMessage = null,
                            isDestinationScreenLoading = false,
                            operationTargetPathDirectories = files
                        )
                    }
                },
                onFailure = { message ->
                    _uiState.update {
                        it.copy(
                            errorMessage = message.message,
                            successMessage = null,
                            isDestinationScreenLoading = false
                        )
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
            renameFileUseCase(selectedFile.path, uiState.value.newFileName).fold(
                onSuccess = { message ->
                    _uiState.update {
                        it.copy(
                            successMessage = message,
                            errorMessage = null,
                            showRenameDialog = false,
                            newFileName = "",
                            selectedFile = null
                        )
                    }
                },
                onFailure = { errorMessage ->
                    _uiState.update {
                        it.copy(
                            errorMessage = errorMessage.message,
                            successMessage = null,
                            showRenameDialog = false,
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
            copyFileUseCase(filePath, destPath).fold(
                onSuccess = { message ->
                    _uiState.update {
                        it.copy(
                            successMessage = message,
                            errorMessage = null
                        )
                    }

                    clearAllOperationsState()
                },
                onFailure = { errorMessage ->
                    _uiState.update { it.copy(errorMessage = errorMessage.message, successMessage = null) }

                    clearAllOperationsState()
                }
            )
        }
    }

    fun moveFile(filePath: String, destPath: String) {
        viewModelScope.launch {
            moveFileUseCase(filePath, destPath).fold(
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
            createFolderUseCase(uiState.value.operationTargetPath, folderName).fold(
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
                    _uiState.update { it.copy(newFolderError = exception.message) }
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
        if (fileOperation == FileOperations.NONE) return
        else _uiState.update {
            it.copy(selectedOperation = fileOperation)
        }
    }

    fun navigateToFolder(folderPath: String) {
        // Add current path to history before navigating
        val currentPath = _uiState.value.operationTargetPath
        if (currentPath.isNotEmpty() && folderHistory.lastOrNull() != currentPath) {
            folderHistory.add(currentPath)
        }

        updateCurrentPath(folderPath)
        getDirectories(folderPath)
    }

    fun navigateBack(): Boolean {
        // ✅ Return true if we handled the back press, false if we should exit
        return if (folderHistory.isNotEmpty()) {
            val previousPath = folderHistory.removeAt(folderHistory.lastIndex)
            updateCurrentPath(previousPath)
            getDirectories(previousPath)
            true
        } else {
            // We're at the root, let the system handle back press (exit DestinationScreen)
            false
        }
    }

    fun initializeDestinationScreen(startPath: String) {
        folderHistory.clear()
        updateCurrentPath(startPath)
        getDirectories(startPath)
    }

    fun clearAllOperationsState() {
        _uiState.update {
            it.copy(
                selectedOperation = FileOperations.NONE,
                operationTargetPath = Environment.getExternalStorageDirectory().absolutePath,
                operationTargetPathDirectories = emptyList()
            )
        }
        // Clear folder history too
        folderHistory.clear()
    }


    fun toggleRenameDialog() {
        _uiState.update {
            it.copy(
                showRenameDialog = !uiState.value.showRenameDialog,
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

    fun updateCurrentPath(newCurrPath: String) {
        _uiState.update { it.copy(operationTargetPath = newCurrPath) }
    }
}
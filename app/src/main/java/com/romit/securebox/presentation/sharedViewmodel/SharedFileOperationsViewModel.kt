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
    private val moveFileUseCase: MoveFileUseCase,
) :
    ViewModel() {
    private val _uiState = MutableStateFlow(SharedFileOperationsUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = Channel<SharedFileOperationsUiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    fun deleteFile(filePath: String) {
        viewModelScope.launch {
            deleteFileUseCase(filePath).fold(
                onSuccess = { message ->
                    _uiEvents.send(SharedFileOperationsUiEvent.ShowSnackBar(message))
                }
            ) { exception ->
                _uiEvents.send(
                    SharedFileOperationsUiEvent.ShowSnackBar(
                        exception.message ?: "Failed to delete"
                    )
                )
            }
        }
    }

    fun getDirectories(dirPath: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDestinationScreenLoading = true
                )
            }
            getDirectoriesUseCase(dirPath).fold(
                onSuccess = { files ->
                    _uiState.update {
                        it.copy(
                            isDestinationScreenLoading = false,
                            operationTargetPathDirectories = files
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isDestinationScreenLoading = false
                        )
                    }
                    _uiEvents.send(SharedFileOperationsUiEvent.ShowSnackBar(exception.message ?: "Failed To Load"))
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
                            showRenameDialog = false,
                            newFileName = "",
                            selectedFile = null
                        )
                    }
                    _uiEvents.send(SharedFileOperationsUiEvent.ShowSnackBar(message))
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            showRenameDialog = false,
                            newFileName = "",
                            selectedFile = null
                        )
                    }
                    _uiEvents.send(SharedFileOperationsUiEvent.ShowSnackBar(exception.message ?: "Failed to rename file"))
                }
            )
        }
    }

    fun copyFile(filePath: String, destPath: String) {
        viewModelScope.launch {
            copyFileUseCase(filePath, destPath).fold(
                onSuccess = { message ->
                    _uiEvents.send(SharedFileOperationsUiEvent.ShowSnackBar(message))

                    clearAllOperationsState()
                },
                onFailure = { exception ->
                    _uiEvents.send(SharedFileOperationsUiEvent.ShowSnackBar(exception.message ?: "Failed to rename file"))
                    clearAllOperationsState()
                }
            )
        }
    }

    fun moveFile(filePath: String, destPath: String) {
        viewModelScope.launch {
            moveFileUseCase(filePath, destPath).fold(
                onSuccess = { message ->
                    _uiEvents.send(SharedFileOperationsUiEvent.ShowSnackBar(message))

                    clearAllOperationsState()
                },
                onFailure = { exception ->
                    _uiEvents.send(SharedFileOperationsUiEvent.ShowSnackBar(exception.message ?: "Failed to rename file"))

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
                            showCreateFolderDialog = false,
                            newFolderName = ""
                        )
                    }
                    _uiEvents.send(SharedFileOperationsUiEvent.ShowSnackBar(message))
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

    fun clearAllOperationsState() {
        _uiState.update {
            it.copy(
                selectedOperation = FileOperations.NONE,
                operationTargetPath = Environment.getExternalStorageDirectory().absolutePath,
                operationTargetPathDirectories = emptyList()
            )
        }
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

    fun updateOperationPathAndFetchDirectories(newCurrPath: String) {
        _uiState.update { it.copy(operationTargetPath = newCurrPath) }
        getDirectories(newCurrPath)
    }
}
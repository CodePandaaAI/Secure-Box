package com.romit.securebox.components

import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.romit.securebox.navigation.Screen
import com.romit.securebox.presentation.sharedViewmodel.NavigationViewModel
import com.romit.securebox.presentation.sharedViewmodel.SharedFileOperationsViewModel
import com.romit.securebox.util.FileOperations

@Composable
fun DialogsAndBottomSheet() {
    val activity = LocalActivity.current as ComponentActivity
    val sharedFileOperationsViewModel = hiltViewModel<SharedFileOperationsViewModel>(viewModelStoreOwner = activity)
    val navigationViewModel = hiltViewModel<NavigationViewModel>(viewModelStoreOwner = activity)
    val uiState by sharedFileOperationsViewModel.uiState.collectAsState()

    if (uiState.selectedFile != null && navigationViewModel.currentScreen() !is Screen.DestinationScreen) {
        BottomFileInfoSheet(
            onDismiss = { sharedFileOperationsViewModel.selectedFileForBottomSheet(null) },
            onOpenDeleteDialog = { sharedFileOperationsViewModel.toggleDeleteDialog() },
            onOpenRenameDialog = { sharedFileOperationsViewModel.toggleRenameDialog() },
            selectedFile = { uiState.selectedFile!! },
            onCopyTo = {
                sharedFileOperationsViewModel.clearAllOperationsState()
                sharedFileOperationsViewModel.chooseOperation(FileOperations.COPY)
                navigationViewModel.navigateTo(
                    Screen.DestinationScreen(
                        folderPath = Environment.getExternalStorageDirectory().absolutePath
                    )
                )
                sharedFileOperationsViewModel.updateOperationPathAndFetchDirectories(
                    Environment.getExternalStorageDirectory().absolutePath
                )
            },
            onMoveTo = {
                sharedFileOperationsViewModel.clearAllOperationsState()
                sharedFileOperationsViewModel.chooseOperation(FileOperations.MOVE)
                navigationViewModel.navigateTo(
                    Screen.DestinationScreen(
                        folderPath = Environment.getExternalStorageDirectory().absolutePath
                    )
                )
                sharedFileOperationsViewModel.updateOperationPathAndFetchDirectories(
                    Environment.getExternalStorageDirectory().absolutePath
                )
            }
        )
    }
    // Rename Dialog
    if (uiState.showRenameDialog && uiState.selectedFile != null) {
        RenameDialog(
            onDismissRequest = { sharedFileOperationsViewModel.toggleRenameDialog() },
            onCancel = { sharedFileOperationsViewModel.toggleRenameDialog() },
            onRenamingFile = { sharedFileOperationsViewModel.onRenamingFile(it) },
            onRenameFileClicked = { sharedFileOperationsViewModel.onRenameFileClicked() },
            newFileName = { uiState.renameInput },
            selectedFile = { uiState.selectedFile!! }
        )
    }

    // Delete Dialog
    if (uiState.showDeleteDialog && uiState.selectedFile != null) {
        DeleteDialog(
            onDismissRequest = { sharedFileOperationsViewModel.toggleDeleteDialog() },
            onConfirmDelete = {
                sharedFileOperationsViewModel.deleteFile(uiState.selectedFile!!.path)
                sharedFileOperationsViewModel.toggleDeleteDialog()
                sharedFileOperationsViewModel.selectedFileForBottomSheet(null)
            },
            selectedFile = { uiState.selectedFile!! }
        )
    }

    if (uiState.showCreateFolderDialog) {
        CreateFolderDialog(
            folderName = uiState.newFolderName,
            error = uiState.newFolderError,
            onFolderNameChange = {
                sharedFileOperationsViewModel.updateNewFolderName(
                    it
                )
            },
            onConfirmFolderCreation = { sharedFileOperationsViewModel.createFolder() },
            onDismissFolderDialog = { sharedFileOperationsViewModel.toggleCreateFolderDialog() }
        )
    }
}
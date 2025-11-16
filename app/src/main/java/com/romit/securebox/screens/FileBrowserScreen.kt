package com.romit.securebox.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.romit.securebox.R
import com.romit.securebox.components.BottomFileInfoSheet
import com.romit.securebox.components.DeleteDialog
import com.romit.securebox.components.FileCard
import com.romit.securebox.components.RenameDialog
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.data.model.SharedFileOperationsUiState
import com.romit.securebox.util.getListItemShape
import com.romit.securebox.viewmodels.FileBrowserScreenViewModel
import com.romit.securebox.viewmodels.SharedFileOperationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileBrowserScreen(
    modifier: Modifier = Modifier,
    sharedFileOperationsUiState: SharedFileOperationsUiState,
    path: String,
    onFileClicked: (FileItem) -> Unit,
    fileBrowserScreenViewModel: FileBrowserScreenViewModel,
    sharedFileOperationsViewModel: SharedFileOperationsViewModel,
    onCopyTo: (FileItem) -> Unit,
    onMoveTo: (FileItem) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val uiState by fileBrowserScreenViewModel.uiState.collectAsState()

    LaunchedEffect(path) {
        fileBrowserScreenViewModel.getDirFiles(path)
    }
    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            fileBrowserScreenViewModel.clearMessages()
        }
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            fileBrowserScreenViewModel.clearMessages()
        }
    }
    when {
        uiState.isLoading -> {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.browsingPathDirectories.isNotEmpty() -> {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer),
                contentPadding = PaddingValues(16.dp)
            ) {
                itemsIndexed(
                    items = uiState.browsingPathDirectories,
                    key = { _, file -> file.path }
                ) { index, file ->
                    FileCard(
                        modifier = Modifier.padding(vertical = 1.dp),
                        file = file,
                        onFileClick = { onFileClicked(it) },
                        onFileOperation = { fileItem ->
                            sharedFileOperationsViewModel.selectedFileForBottomSheet(fileItem)
                        },
                        onFileLongClick = { fileItem ->
                            sharedFileOperationsViewModel.selectedFileForBottomSheet(fileItem)
                        },
                        shape = getListItemShape(
                            index = index,
                            totalItems = uiState.browsingPathDirectories.size
                        )
                    )
                }
            }
        }

        else -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.empty),
                    contentDescription = null,
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp))
                Text("Empty")
            }
        }
    }
    // Bottom Sheet
    if (sharedFileOperationsUiState.selectedFile != null) {
        BottomFileInfoSheet(
            onDismiss = { sharedFileOperationsViewModel.selectedFileForBottomSheet(null) },
            onOpenDeleteDialog = { sharedFileOperationsViewModel.toggleDeleteDialog() },
            onOpenRenameDialog = { sharedFileOperationsViewModel.toggleRenameDialog() },
            onCopyTo = { onCopyTo(it) },
            onMoveTo = { onMoveTo(it) },
            selectedFile = { sharedFileOperationsUiState.selectedFile }
        )
    }

    // Rename Dialog
    if (sharedFileOperationsUiState.showRenameInput && sharedFileOperationsUiState.selectedFile != null) {
        RenameDialog(
            onDismissRequest = { sharedFileOperationsViewModel.toggleRenameDialog() },
            onCancel = { sharedFileOperationsViewModel.toggleRenameDialog() },
            onRenamingFile = { sharedFileOperationsViewModel.onRenamingFile(it) },
            onRenameFileClicked = { sharedFileOperationsViewModel.onRenameFileClicked() },
            newFileName = { sharedFileOperationsUiState.newFileName },
            selectedFile = { sharedFileOperationsUiState.selectedFile }
        )
    }

    // Delete Dialog
    if (sharedFileOperationsUiState.showDeleteDialog && sharedFileOperationsUiState.selectedFile != null) {
        DeleteDialog(
            onDismissRequest = { sharedFileOperationsViewModel.toggleDeleteDialog() },
            onConfirmDelete = {
                sharedFileOperationsViewModel.deleteFile(sharedFileOperationsUiState.selectedFile.path)
                sharedFileOperationsViewModel.toggleDeleteDialog()
                sharedFileOperationsViewModel.selectedFileForBottomSheet(null)
            },
            selectedFile = { sharedFileOperationsUiState.selectedFile }
        )
    }
}
package com.romit.securebox.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.romit.securebox.viewmodels.FileBrowserScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileBrowserScreen(
    modifier: Modifier = Modifier,
    path: String,
    onFileClicked: (FileItem) -> Unit,
    viewModel: FileBrowserScreenViewModel,
    snackbarHostState: SnackbarHostState
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(path) {
        viewModel.getDirFiles(path)
    }
    LaunchedEffect(uiState.successMessage, uiState.error) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessages()
        }
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearMessages()
        }
    }
    when {
        uiState.isLoading -> {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.dirFiles.isNotEmpty() -> {
            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                items(uiState.dirFiles, key = { file -> file.path }) { file ->
                    FileCard(
                        file = file,
                        onFileClick = { file -> onFileClicked(file) },
                        onFileOperation = { fileItem ->
                            viewModel.selectedFileForBottomSheet(fileItem)
                        },
                        onFileLongClick = { fileItem ->
                            viewModel.selectedFileForBottomSheet(fileItem)
                        }
                    )
                }
            }
        }

        else -> {
            Column(
                modifier = modifier.fillMaxSize(),
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
    if (uiState.selectedFile != null) {
        BottomFileInfoSheet(
            onDismiss = { viewModel.selectedFileForBottomSheet(null) },
            onOpenDeleteDialog = { viewModel.toggleDeleteDialog() },
            onOpenRenameDialog = { viewModel.toggleRenameDialog() },
            selectedFile = { uiState.selectedFile!! }
        )
    }

    // Rename Dialog
    if (uiState.isRenameEnabled && uiState.selectedFile != null) {
        RenameDialog(
            onDismissRequest = { viewModel.toggleRenameDialog() },
            onCancel = { viewModel.toggleRenameDialog() },
            onRenamingFile = { viewModel.onRenamingFile(it) },
            onRenameFileClicked = { viewModel.onRenameFileClicked() },
            newFileName = { uiState.newFileName },
            selectedFile = { uiState.selectedFile!! }
        )
    }

    // Delete Dialog
    if (uiState.showDeleteDialog && uiState.selectedFile != null) {
        DeleteDialog(
            onDismissRequest = { viewModel.toggleDeleteDialog() },
            onConfirmDelete = {
                viewModel.deleteFile(uiState.selectedFile!!.path)
                viewModel.toggleDeleteDialog()
                viewModel.selectedFileForBottomSheet(null)
            },
            selectedFile = { uiState.selectedFile!! }
        )
    }
}
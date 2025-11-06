package com.romit.securebox.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.romit.securebox.components.FileCard
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.viewmodels.AllRecentsScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllRecentsScreen(
    viewModel: AllRecentsScreenViewModel = hiltViewModel(),
    onFileClicked: (FileItem) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val uiState by viewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()

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

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(8.dp)
    ) {
        items(uiState.files, key = { file -> file.path }) { file ->
            FileCard(
                file = file,
                onFileClick = { onFileClicked(file) },
                onFileOperation = { fileItem ->
                    viewModel.selectedFileForBottomSheet(fileItem)
                },
                onFileLongClick = { fileItem ->
                    viewModel.selectedFileForBottomSheet(fileItem)
                }
            )
        }

        // Show the spinner at the bottom when loading
        if (uiState.isLoadingNextPage) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    // This is the "trigger"
    val isScrolledToEnd =
        lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == uiState.files.size - 1

    LaunchedEffect(isScrolledToEnd) {
        if (isScrolledToEnd && !uiState.isLoadingNextPage) {
            viewModel.loadNextPage()
        }
    }
    // Bottom Sheet
    if (uiState.selectedFile != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.selectedFileForBottomSheet(null) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Preview
                when {
                    uiState.selectedFile!!.isImage -> {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            AsyncImage(
                                model = uiState.selectedFile?.path ?: "",
                                contentDescription = uiState.selectedFile?.name ?: "",
                                modifier = Modifier
                                    .size(180.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    uiState.selectedFile!!.isDirectory -> {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Folder,
                                contentDescription = "Folder",
                                modifier = Modifier
                                    .padding(24.dp)
                                    .size(72.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    else -> {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "File",
                                modifier = Modifier
                                    .padding(24.dp)
                                    .size(72.dp)
                            )
                        }
                    }
                }

                Text(
                    text = uiState.selectedFile!!.name,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                ListItem(
                    headlineContent = { Text("Rename") },
                    leadingContent = { Icon(Icons.Default.Edit, null) },
                    modifier = Modifier.clickable {
                        viewModel.toggleRenameDialog()
                    }
                )

                ListItem(
                    headlineContent = { Text("Delete") },
                    leadingContent = { Icon(Icons.Default.Delete, null) },
                    modifier = Modifier.clickable {
                        viewModel.toggleDeleteDialog()
                    }
                )
            }
        }
    }

    // Rename Dialog
    if (uiState.isRenameEnabled && uiState.selectedFile != null) {
        Dialog(onDismissRequest = { viewModel.toggleRenameDialog() }) {
            Column(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        RoundedCornerShape(28.dp)
                    )
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Rename", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = uiState.newFileName,
                    onValueChange = { viewModel.onRenamingFile(it) },
                    label = { Text("New name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { viewModel.toggleRenameDialog() }) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { viewModel.onRenameFileClicked() },
                        enabled = uiState.newFileName.isNotBlank() &&
                                uiState.newFileName != uiState.selectedFile!!.name
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

    // Delete Dialog
    if (uiState.showDeleteDialog && uiState.selectedFile != null) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleDeleteDialog() },
            title = { Text("Delete ${uiState.selectedFile!!.name}?") },
            text = {
                Text(
                    if (uiState.selectedFile!!.isDirectory) {
                        "This folder and all its contents will be permanently deleted."
                    } else {
                        "This file will be permanently deleted."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFile(uiState.selectedFile!!.path)
                        viewModel.toggleDeleteDialog()
                        viewModel.selectedFileForBottomSheet(null)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.toggleDeleteDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
}
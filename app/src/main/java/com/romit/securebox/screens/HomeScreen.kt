package com.romit.securebox.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.romit.securebox.components.FileCard
import com.romit.securebox.components.StorageCategoryCard
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.viewmodels.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCategoryClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFileClicked: (FileItem) -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState
) {
    val uiState by viewModel.uiState.collectAsState()

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
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            uiState.recentFiles.isNotEmpty() -> {
                Text(
                    text = "Recents",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(Modifier.height(16.dp))
                uiState.recentFiles.forEach { file ->
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

            else -> {
                Text("No Recent Files", style = MaterialTheme.typography.titleLarge)
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "Categories",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Spacer(Modifier.height(16.dp))
        if (uiState.storageCategoriesList.isEmpty()) {
            CircularProgressIndicator()
            return@Column
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StorageCategoryCard(
                uiState.storageCategoriesList[0],
                onCategoryClick = { onCategoryClicked(it) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            StorageCategoryCard(
                uiState.storageCategoriesList[1],
                onCategoryClick = { onCategoryClicked(it) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StorageCategoryCard(
                uiState.storageCategoriesList[2],
                onCategoryClick = { onCategoryClicked(it) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            StorageCategoryCard(
                uiState.storageCategoriesList[3],
                onCategoryClick = { onCategoryClicked(it) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StorageCategoryCard(
                uiState.storageCategoriesList[4],
                onCategoryClick = { onCategoryClicked(it) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            StorageCategoryCard(
                uiState.storageCategoriesList[5],
                onCategoryClick = { onCategoryClicked(it) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
    }
    if (uiState.selectedFile != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.selectedFileForBottomSheet(null) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    uiState.selectedFile!!.isImage -> {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            AsyncImage(
                                model = uiState.selectedFile?.path ?: "",
                                contentDescription = uiState.selectedFile?.name ?: "",
                                modifier = modifier
                                    .size(192.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentScale = ContentScale.Crop,
                                onLoading = {}
                            )
                        }
                    }

                    uiState.selectedFile!!.isDirectory -> {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Folder,
                                contentDescription = "Folder",
                                modifier = Modifier
                                    .padding(64.dp)
                                    .size(192.dp)
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
                                    .padding(16.dp)
                                    .size(128.dp)
                            )
                        }

                    }
                }
                Text(
                    text = uiState.selectedFile!!.name,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                HorizontalDivider()

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

    if (uiState.isRenameEnabled && uiState.selectedFile != null) {
        Dialog(onDismissRequest = { viewModel.toggleRenameDialog() }) {
            Column(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        RoundedCornerShape(20.dp)
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
                    OutlinedButton(onClick = { viewModel.toggleRenameDialog() }) {
                        Text("Cancel")
                    }
                    OutlinedButton(
                        onClick = { viewModel.onRenameFileClicked() },  // ✅ Don't toggle here
                        enabled = uiState.newFileName.isNotBlank() &&
                                uiState.newFileName != uiState.selectedFile!!.name
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

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
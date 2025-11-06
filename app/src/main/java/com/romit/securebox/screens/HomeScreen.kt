package com.romit.securebox.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
    modifier: Modifier = Modifier,
    onCategoryClicked: (String) -> Unit,
    onShowAllRecents: () -> Unit = {},
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
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 16.dp),
    ) {
        // Recents Section
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.recentFiles.isNotEmpty() -> {
                // Section Header with "Show All" button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recents",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        onClick = onShowAllRecents,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.clip(RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Show all",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Show all",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Recent Files List
                uiState.recentFiles.forEach { file ->
                    FileCard(
                        file = file,
                        onFileClick = { onFileClicked(it) },
                        onFileOperation = { viewModel.selectedFileForBottomSheet(it) },
                        onFileLongClick = { viewModel.selectedFileForBottomSheet(it) }
                    )
                }
            }

            else -> {
                // Empty state
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "No recent files",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Files you open will appear here",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Categories Section
        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(Modifier.height(8.dp))

        if (uiState.storageCategoriesList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Category Grid (cleaner with Column for rows)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Row 1
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StorageCategoryCard(
                        uiState.storageCategoriesList[0],
                        onCategoryClick = onCategoryClicked,
                        modifier = Modifier.weight(1f)
                    )
                    StorageCategoryCard(
                        uiState.storageCategoriesList[1],
                        onCategoryClick = onCategoryClicked,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StorageCategoryCard(
                        uiState.storageCategoriesList[2],
                        onCategoryClick = onCategoryClicked,
                        modifier = Modifier.weight(1f)
                    )
                    StorageCategoryCard(
                        uiState.storageCategoriesList[3],
                        onCategoryClick = onCategoryClicked,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 3
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StorageCategoryCard(
                        uiState.storageCategoriesList[4],
                        onCategoryClick = onCategoryClicked,
                        modifier = Modifier.weight(1f)
                    )
                    StorageCategoryCard(
                        uiState.storageCategoriesList[5],
                        onCategoryClick = onCategoryClicked,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
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
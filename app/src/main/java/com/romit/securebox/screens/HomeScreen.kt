package com.romit.securebox.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.romit.securebox.components.BottomFileInfoSheet
import com.romit.securebox.components.DeleteDialog
import com.romit.securebox.components.FileCard
import com.romit.securebox.components.RenameDialog
import com.romit.securebox.components.StorageCategoryCard
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.data.model.SharedFileOperationsUiState
import com.romit.securebox.util.getListItemShape
import com.romit.securebox.viewmodels.HomeScreenViewModel
import com.romit.securebox.viewmodels.SharedFileOperationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    sharedFileOperationsUiState: SharedFileOperationsUiState,
    modifier: Modifier = Modifier,
    onCategoryClicked: (String) -> Unit,
    onShowAllRecents: () -> Unit,
    onFileClicked: (FileItem) -> Unit,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    sharedFileOperationsViewModel: SharedFileOperationsViewModel,
    onCopyTo: (FileItem) -> Unit,
    onMoveTo: (FileItem) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val uiState by homeScreenViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            homeScreenViewModel.clearMessages()
        }
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            homeScreenViewModel.clearMessages()
        }
    }

    // ✅ Observe SHARED OPERATIONS messages (delete, rename, copy, move)
    LaunchedEffect(sharedFileOperationsUiState.successMessage, sharedFileOperationsUiState.errorMessage) {
        sharedFileOperationsUiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            sharedFileOperationsViewModel.clearMessages()
            homeScreenViewModel.getRecentFiles()
        }
        sharedFileOperationsUiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            sharedFileOperationsViewModel.clearMessages()
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { homeScreenViewModel.getRecentFiles() },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp, horizontal = 16.dp)
        ) {
            // Recents Section
            when {
                uiState.isRefreshing -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.recentFilesList.isNotEmpty() -> {
                    // Section Header with "Show All" button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
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
                            color = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else Color.Gray.copy(
                                alpha = 0.1f
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.clip(RoundedCornerShape(16.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
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

                    Spacer(Modifier.height(8.dp))

                    // Recent Files List
                    uiState.recentFilesList.forEachIndexed { index, file ->
                        FileCard(
                            modifier = Modifier.padding(vertical = 1.dp),
                            file = file,
                            onFileClick = { onFileClicked(it) },
                            onFileOperation = {
                                sharedFileOperationsViewModel.selectedFileForBottomSheet(
                                    it
                                )
                            },
                            onFileLongClick = {
                                sharedFileOperationsViewModel.selectedFileForBottomSheet(
                                    it
                                )
                            },
                            shape = getListItemShape(
                                index = index,
                                totalItems = uiState.recentFilesList.size
                            )
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
                                text = "No recent allRecents",
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

            if (uiState.storageCategories.isEmpty()) {
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
                            uiState.storageCategories[0],
                            onCategoryClick = onCategoryClicked,
                            modifier = Modifier.weight(1f)
                        )
                        StorageCategoryCard(
                            uiState.storageCategories[1],
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
                            uiState.storageCategories[2],
                            onCategoryClick = onCategoryClicked,
                            modifier = Modifier.weight(1f)
                        )
                        StorageCategoryCard(
                            uiState.storageCategories[3],
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
                            uiState.storageCategories[4],
                            onCategoryClick = onCategoryClicked,
                            modifier = Modifier.weight(1f)
                        )
                        StorageCategoryCard(
                            uiState.storageCategories[5],
                            onCategoryClick = onCategoryClicked,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Bottom Sheet
    if (sharedFileOperationsUiState.selectedFile != null) {
        BottomFileInfoSheet(
            onDismiss = { sharedFileOperationsViewModel.selectedFileForBottomSheet(null) },
            onOpenDeleteDialog = { sharedFileOperationsViewModel.toggleDeleteDialog() },
            onOpenRenameDialog = { sharedFileOperationsViewModel.toggleRenameDialog() },
            selectedFile = { sharedFileOperationsUiState.selectedFile },
            onCopyTo = { onCopyTo(it) },
            onMoveTo = { onMoveTo(it) }
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
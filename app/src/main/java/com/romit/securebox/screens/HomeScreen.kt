package com.romit.securebox.screens

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
import com.romit.securebox.viewmodels.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onCategoryClicked: (String) -> Unit,
    onShowAllRecents: () -> Unit,
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
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { viewModel.getRecentFiles() },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 16.dp),
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
                            color = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceContainer else Color.Gray.copy(
                                alpha = 0.2f
                            ),
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
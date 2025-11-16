package com.romit.securebox.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.romit.securebox.components.BottomFileInfoSheet
import com.romit.securebox.components.DeleteDialog
import com.romit.securebox.components.FileCard
import com.romit.securebox.components.RenameDialog
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.util.getListItemShape
import com.romit.securebox.viewmodels.AllRecentsScreenViewModel
import com.romit.securebox.viewmodels.FileBrowserScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllRecentsScreen(
    viewModel: AllRecentsScreenViewModel = hiltViewModel(),
    fileBrowserViewModel: FileBrowserScreenViewModel,
    onFileClicked: (FileItem) -> Unit,
    snackbarHostState: SnackbarHostState,
    onCopyTo: (FileItem) -> Unit,
    onMoveTo: (FileItem) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val fileBrowserUiState by fileBrowserViewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(fileBrowserUiState.successMessage, fileBrowserUiState.errorMessage) {
        fileBrowserUiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessages()
        }
        fileBrowserUiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearMessages()
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surfaceContainer),
            state = lazyListState,
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(
                items = uiState.files,
                key = { _, file -> file.path }
            ) { index, file ->
                FileCard(
                    modifier = Modifier.padding(vertical = 1.dp),
                    file = file,
                    onFileClick = { onFileClicked(it) },
                    onFileOperation = { fileItem ->
                        fileBrowserViewModel.selectedFileForBottomSheet(fileItem)
                    },
                    onFileLongClick = { fileItem ->
                        fileBrowserViewModel.selectedFileForBottomSheet(fileItem)
                    },
                    // ✅ Add shape based on position
                    shape = getListItemShape(
                        index = index,
                        totalItems = uiState.files.size
                    )
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
    }

    // This is the "trigger"
    val isScrolledToEnd by remember {
        derivedStateOf {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == uiState.files.size - 1
        }
    }

    LaunchedEffect(isScrolledToEnd) {
        if (isScrolledToEnd && !uiState.isLoadingNextPage) {
            viewModel.loadNextPage()
        }
    }
    // Bottom Sheet
    if (fileBrowserUiState.selectedFile != null) {
        BottomFileInfoSheet(
            onDismiss = { viewModel.selectedFileForBottomSheet(null) },
            onOpenDeleteDialog = { viewModel.toggleDeleteDialog() },
            onOpenRenameDialog = { viewModel.toggleRenameDialog() },
            selectedFile = { fileBrowserUiState.selectedFile!! },
            onCopyTo = { onCopyTo(it) },
            onMoveTo = { onMoveTo(it) }
        )
    }

    // Rename Dialog
    if (fileBrowserUiState.showRenameInput && fileBrowserUiState.selectedFile != null) {
        RenameDialog(
            onDismissRequest = { viewModel.toggleRenameDialog() },
            onCancel = { viewModel.toggleRenameDialog() },
            onRenamingFile = { viewModel.onRenamingFile(it) },
            onRenameFileClicked = { viewModel.onRenameFileClicked() },
            newFileName = { fileBrowserUiState.newFileName },
            selectedFile = { fileBrowserUiState.selectedFile!! }
        )
    }

    // Delete Dialog
    if (fileBrowserUiState.showDeleteDialog && fileBrowserUiState.selectedFile != null) {
        DeleteDialog(
            onDismissRequest = { viewModel.toggleDeleteDialog() },
            onConfirmDelete = {
                viewModel.deleteFile(fileBrowserUiState.selectedFile!!.path)
                viewModel.toggleDeleteDialog()
                viewModel.selectedFileForBottomSheet(null)
            },
            selectedFile = { fileBrowserUiState.selectedFile!! }
        )
    }
}
package com.romit.securebox.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.romit.securebox.components.BottomFileInfoSheet
import com.romit.securebox.components.DeleteDialog
import com.romit.securebox.components.FileCard
import com.romit.securebox.components.RenameDialog
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

    // ✅ ONLY CHANGE: Wrap with PullToRefreshBox
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
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
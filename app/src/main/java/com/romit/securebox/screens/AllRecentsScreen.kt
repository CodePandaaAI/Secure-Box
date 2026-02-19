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
import com.romit.securebox.components.FileCard
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.data.model.SharedFileOperationsUiState
import com.romit.securebox.util.getListItemShape
import com.romit.securebox.viewmodels.AllRecentsScreenViewModel
import com.romit.securebox.viewmodels.SharedViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllRecentsScreen(
    sharedFileOperationsUiState: SharedFileOperationsUiState,
    recentsScreenViewModel: AllRecentsScreenViewModel = hiltViewModel(),
    onFileClicked: (FileItem) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val sharedFileOperationsViewModel = SharedViewModelProvider.current
    val uiState by recentsScreenViewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()

    val isScrolledToEnd by remember {
        derivedStateOf {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == uiState.allRecents.size - 1
        }
    }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            recentsScreenViewModel.clearMessages()
        }
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            recentsScreenViewModel.clearMessages()
        }
    }

    LaunchedEffect(sharedFileOperationsUiState.successMessage, sharedFileOperationsUiState.errorMessage) {
        sharedFileOperationsUiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            sharedFileOperationsViewModel.clearMessages()
            recentsScreenViewModel.refresh()
        }
        sharedFileOperationsUiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            sharedFileOperationsViewModel.clearMessages()
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { recentsScreenViewModel.refresh() },
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
                items = uiState.allRecents,
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
                        totalItems = uiState.allRecents.size
                    )
                )
            }

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

    LaunchedEffect(isScrolledToEnd) {
        if (isScrolledToEnd && !uiState.isLoadingNextPage && !uiState.isPaginationEndReached) {
            recentsScreenViewModel.loadNextPage()
        }
    }
}
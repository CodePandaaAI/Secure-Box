package com.romit.securebox.presentation.featureRecents

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
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
import com.romit.securebox.presentation.featureRecents.components.AllRecentsErrorScreen
import com.romit.securebox.presentation.featureRecents.components.AllRecentsLoadingScreen
import com.romit.securebox.util.getListItemShape
import com.romit.securebox.presentation.sharedViewmodel.SharedFileOperationsViewModel


@Composable
fun AllRecentsScreen(
    onFileClicked: (FileItem) -> Unit
) {
    val activity = LocalActivity.current as ComponentActivity
    val sharedFileOperationsViewModel = hiltViewModel<SharedFileOperationsViewModel>(viewModelStoreOwner = activity)
    val recentsScreenViewModel = hiltViewModel<AllRecentsScreenViewModel>()
    val uiState by recentsScreenViewModel.uiState.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }

    val lazyListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        recentsScreenViewModel.uiEvent.collect { event ->
            if (event is AllRecentsUiEvent.ShowSnackBar) snackBarHostState.showSnackbar(event.message)
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState is AllRecentsUiState.Loading,
        onRefresh = { recentsScreenViewModel.reloadRecentFiles() },
        modifier = Modifier.fillMaxSize()
    ) {
        when (val state = uiState) {
            is AllRecentsUiState.Loading -> {
                AllRecentsLoadingScreen()
            }

            is AllRecentsUiState.Success -> {
                val reachedBottom by remember(state.allRecents.size) {
                    derivedStateOf {
                        val lastVisibleItem = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()
                        lastVisibleItem != null && lastVisibleItem.index != -1 && lastVisibleItem.index == state.allRecents.size - 1
                    }
                }
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surfaceContainer),
                    state = lazyListState,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    itemsIndexed(
                        items = state.allRecents,
                        key = { _, file -> file.path }
                    ) { index, file ->
                        FileCard(
                            file = file,
                            onOpenFile = { onFileClicked(it) },
                            onSelectFileForBottomSheet = { fileItem ->
                                sharedFileOperationsViewModel.selectedFileForBottomSheet(fileItem)
                            },
                            shape = getListItemShape(
                                index = index,
                                totalItems = state.allRecents.size
                            )
                        )
                    }

                    if (state.isLoadingNextPage) {
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
                LaunchedEffect(reachedBottom) {
                    if (reachedBottom && !state.isLoadingNextPage && !state.isPaginationEndReached) {
                        recentsScreenViewModel.loadNextPage()
                    }
                }
            }

            is AllRecentsUiState.Error -> {
                AllRecentsErrorScreen()
            }
        }
    }
}
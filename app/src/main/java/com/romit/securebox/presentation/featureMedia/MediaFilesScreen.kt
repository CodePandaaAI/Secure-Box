package com.romit.securebox.presentation.featureMedia

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.romit.securebox.R
import com.romit.securebox.components.file.FileCard
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.data.model.StorageCategoryType
import com.romit.securebox.presentation.sharedViewmodel.SharedFileOperationsViewModel
import com.romit.securebox.util.getListItemShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaFilesScreen(
    type: StorageCategoryType,
    onFileClicked: (FileItem) -> Unit
) {
    val activity = LocalActivity.current as ComponentActivity
    val sharedFileOperationsViewModel =
        hiltViewModel<SharedFileOperationsViewModel>(viewModelStoreOwner = activity)
    val mediaFilesViewModel = hiltViewModel<MediaFilesViewModel>()
    val uiState by mediaFilesViewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(type) {
        mediaFilesViewModel.reload(type)
    }

    PullToRefreshBox(
        isRefreshing = uiState is MediaFilesUiState.Loading,
        onRefresh = { mediaFilesViewModel.reload(type) },
        modifier = Modifier.fillMaxSize()
    ) {
        when (val state = uiState) {
            is MediaFilesUiState.Loading -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surfaceContainer),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            is MediaFilesUiState.Success -> {
                if (state.files.isEmpty()) {
                    EmptyMediaScreen(type)
                } else {
                    val reachedBottom by remember(state.files.size) {
                        derivedStateOf {
                            val lastVisibleItem =
                                lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()
                            lastVisibleItem != null &&
                                    lastVisibleItem.index == state.files.size - 1
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.surfaceContainer),
                        state = lazyListState,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        itemsIndexed(
                            items = state.files,
                            key = { _, file -> file.path }
                        ) { index, file ->
                            FileCard(
                                file = file,
                                onOpenFile = { onFileClicked(it) },
                                onSelectFileForBottomSheet = { fileItem ->
                                    sharedFileOperationsViewModel.selectedFileForBottomSheet(
                                        fileItem
                                    )
                                },
                                shape = getListItemShape(
                                    index = index,
                                    totalItems = state.files.size
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
                        if (
                            reachedBottom &&
                            !state.isLoadingNextPage &&
                            !state.isPaginationEndReached
                        ) {
                            mediaFilesViewModel.loadNextPage(type)
                        }
                    }
                }
            }

            is MediaFilesUiState.Error -> {
                EmptyMediaScreen(type)
            }
        }
    }
}

@Composable
private fun EmptyMediaScreen(type: StorageCategoryType) {
    Column(
        Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceContainer),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.empty_state_pet),
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text("No ${type.displayName().lowercase()} found")
    }
}

private fun StorageCategoryType.displayName(): String {
    return when (this) {
        StorageCategoryType.IMAGES -> "Images"
        StorageCategoryType.VIDEOS -> "Videos"
        StorageCategoryType.MUSIC -> "Audio"
        StorageCategoryType.DOCUMENTS -> "Documents"
        StorageCategoryType.DOWNLOADS -> "Downloads"
        StorageCategoryType.INTERNAL_STORAGE -> "Files"
    }
}

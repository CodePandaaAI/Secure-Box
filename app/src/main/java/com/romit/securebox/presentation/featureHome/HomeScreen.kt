package com.romit.securebox.presentation.featureHome

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.presentation.featureHome.components.HomeLoadingScreen
import com.romit.securebox.presentation.featureHome.components.HomeScreenCategoriesContent
import com.romit.securebox.presentation.featureHome.components.HomeScreenEmptyState
import com.romit.securebox.presentation.featureHome.components.HomeScreenRecentsContent
import com.romit.securebox.presentation.sharedViewmodel.SharedFileOperationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onCategoryClicked: (String) -> Unit,
    onShowAllRecents: () -> Unit,
    onOpenFile: (FileItem) -> Unit
) {
    val activity = LocalActivity.current as ComponentActivity
    val sharedFileOperationsViewModel = hiltViewModel<SharedFileOperationsViewModel>(viewModelStoreOwner = activity)

    val homeScreenViewModel: HomeScreenViewModel = hiltViewModel<HomeScreenViewModel>()
    val uiState by homeScreenViewModel.uiState.collectAsState()
    val storageCategories = homeScreenViewModel.storageCategories.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        homeScreenViewModel.uiEvent.collect { event ->
            if (event is HomeUiEvent.ShowSnackBar) snackBarHostState.showSnackbar(event.message)
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState is HomeUiState.RecentFilesLoading,
        onRefresh = { homeScreenViewModel.getRecentFiles() },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Recents Section
            when (val state = uiState) {
                is HomeUiState.RecentFilesLoading -> {
                    HomeLoadingScreen()
                }

                is HomeUiState.Success -> {
                    HomeScreenRecentsContent(
                        state,
                        onOpenFile = {
                            onOpenFile(it)
                        },
                        onSelectFileForBottomSheet = {
                            sharedFileOperationsViewModel.selectedFileForBottomSheet(
                                it
                            )
                        },
                        onShowAllRecents = onShowAllRecents
                    )
                }

                is HomeUiState.Error -> {
                    // Empty state
                    HomeScreenEmptyState()
                }
            }
            // Categories Section
            HomeScreenCategoriesContent(
                { storageCategories.value },
                onCategoryClicked
            )
        }
    }
}
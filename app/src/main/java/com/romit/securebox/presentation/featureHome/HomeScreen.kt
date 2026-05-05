package com.romit.securebox.presentation.featureHome

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.presentation.featureHome.components.HomeLoadingScreen
import com.romit.securebox.presentation.featureHome.components.HomeScreenCategoriesContent
import com.romit.securebox.presentation.featureHome.components.HomeScreenEmptyState
import com.romit.securebox.presentation.featureHome.components.HomeScreenRecentsContent
import com.romit.securebox.viewmodels.SharedFileOperationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onCategoryClicked: (String) -> Unit,
    onShowAllRecents: () -> Unit,
    onOpenFile: (FileItem) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    val sharedFileOperationsViewModel = hiltViewModel<SharedFileOperationsViewModel>()
    val sharedFileOperationsUiState by sharedFileOperationsViewModel.uiState.collectAsState()

    val homeScreenViewModel: HomeScreenViewModel = hiltViewModel<HomeScreenViewModel>()
    val uiState by homeScreenViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let { message ->
            snackBarHostState.showSnackbar(message)
            homeScreenViewModel.clearMessages()
        }
        uiState.errorMessage?.let { error ->
            snackBarHostState.showSnackbar(error)
            homeScreenViewModel.clearMessages()
        }
    }

    LaunchedEffect(
        sharedFileOperationsUiState.successMessage,
        sharedFileOperationsUiState.errorMessage
    ) {
        sharedFileOperationsUiState.successMessage?.let { message ->
            snackBarHostState.showSnackbar(message)
            sharedFileOperationsViewModel.clearMessages()
            homeScreenViewModel.getRecentFiles()
        }
        sharedFileOperationsUiState.errorMessage?.let { error ->
            snackBarHostState.showSnackbar(error)
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
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Recents Section
            when {
                uiState.isRefreshing -> {
                    HomeLoadingScreen()
                }

                uiState.recentFilesList.isNotEmpty() -> {
                    HomeScreenRecentsContent(
                        uiState,
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

                else -> {
                    // Empty state
                    HomeScreenEmptyState()
                }
            }
            HomeScreenCategoriesContent(uiState, onCategoryClicked)
        }
    }
}
package com.romit.securebox.presentation.featureHome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.romit.securebox.components.StorageCategoryCard
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.presentation.featureHome.components.HomeLoadingScreen
import com.romit.securebox.presentation.featureHome.components.HomeScreenContent
import com.romit.securebox.ui.theme.CustomFontFamily
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
                    HomeScreenContent(
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
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
            // Categories Section
            Text(
                text = "Categories",
                fontFamily = CustomFontFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )

            if (uiState.storageCategories.isEmpty()) {
                HomeLoadingScreen()
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
}
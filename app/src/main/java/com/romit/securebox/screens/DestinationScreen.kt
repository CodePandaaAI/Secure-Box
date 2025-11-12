package com.romit.securebox.screens

import android.R.id.message
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.romit.securebox.components.FolderCard
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.viewmodels.DestinationPickerViewModel

@Composable
fun DestinationScreen(
    folderPath: String,
    viewModel: DestinationPickerViewModel,
    onFolderClicked: (FileItem) -> Unit,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(folderPath) {
        viewModel.getDirs(folderPath)
    }

    LaunchedEffect(uiState.success, uiState.error) {
        uiState.success?.let { successMessage ->
            snackbarHostState.showSnackbar(message = successMessage)
            viewModel.clearMessages()
            onNavigateBack()
        }
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(message = error)
            viewModel.clearMessages()
            onNavigateBack()
        }
    }

    when {
        uiState.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        else -> {
            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                items(uiState.directories) { dir ->
                    FolderCard(
                        file = dir,
                        onFolderClick = { onFolderClicked(it) }
                    )
                }
            }
        }
    }
}
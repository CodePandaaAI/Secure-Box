package com.romit.securebox.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.romit.securebox.R
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

        uiState.directories.isNotEmpty() -> {
            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                items(uiState.directories) { dir ->
                    FolderCard(
                        file = dir,
                        onFolderClick = { onFolderClicked(it) }
                    )
                }
            }
        }

        else -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.empty),
                    contentDescription = null,
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp))
                Text("Nothing Here")
            }
        }
    }
}
package com.romit.securebox.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.romit.securebox.R
import com.romit.securebox.components.CreateFolderDialog
import com.romit.securebox.components.FolderCard
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.util.getListItemShape
import com.romit.securebox.viewmodels.SharedFileOperationsViewModel

@Composable
fun DestinationScreen(
    sharedFileOperationsViewModel: SharedFileOperationsViewModel,
    onFolderClicked: (FileItem) -> Unit,
    onNavigateBack: () -> Unit
) {

    val uiState by sharedFileOperationsViewModel.uiState.collectAsState()

    // ✅ Handle back press
    BackHandler(enabled = true) {
        val handledInternally = sharedFileOperationsViewModel.navigateBack()
        if (!handledInternally) {
            // We're at root, exit DestinationScreen
            onNavigateBack()
        }
    }

    when {
        uiState.isDestinationScreenLoading -> {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer
                    ), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.operationTargetPathDirectories.isNotEmpty() -> {
            LazyColumn(
                Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.surfaceContainer), contentPadding = PaddingValues(16.dp)
            ) {
                itemsIndexed(uiState.operationTargetPathDirectories) { index ,dir ->
                    FolderCard(
                        modifier = Modifier.padding(vertical = 1.dp),
                        file = dir,
                        onFolderClick = { onFolderClicked(it) },
                        shape = getListItemShape(
                            index = index,
                            totalItems = uiState.operationTargetPathDirectories.size
                        )
                    )
                }
            }
        }

        else -> {
            Column(
                Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.surfaceContainer),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.empty),
                    contentDescription = null,
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(16.dp))
                Text("Nothing Here")
            }
        }
    }

    if (uiState.showCreateFolderDialog) {
        CreateFolderDialog(
            folderName = uiState.newFolderName,
            error = uiState.newFolderError,
            onFolderNameChange = { sharedFileOperationsViewModel.updateNewFolderName(it) },
            onConfirm = { sharedFileOperationsViewModel.createFolder() },
            onDismiss = { sharedFileOperationsViewModel.toggleCreateFolderDialog() }
        )
    }
}
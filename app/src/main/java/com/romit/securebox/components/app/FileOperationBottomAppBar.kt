package com.romit.securebox.components

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.romit.securebox.navigation.Screen
import com.romit.securebox.presentation.sharedViewmodel.NavigationViewModel
import com.romit.securebox.presentation.sharedViewmodel.SharedFileOperationsViewModel
import com.romit.securebox.util.FileOperations

@Composable
fun FileOperationBottomAppBar(
    onCreateFolder: () -> Unit,
    onConfirmLocation: () -> Unit
) {
    val activity = LocalActivity.current as ComponentActivity
    val sharedFileOperationsViewModel = hiltViewModel<SharedFileOperationsViewModel>(viewModelStoreOwner = activity)
    val navigationViewModel = hiltViewModel<NavigationViewModel>(viewModelStoreOwner = activity)
    val uiState by sharedFileOperationsViewModel.uiState.collectAsState()
    val buttonLabel = when (uiState.selectedOperation) {
        FileOperations.COPY -> "Copy Here"
        FileOperations.MOVE -> "Move Here"
        else -> ""
    }

    if (navigationViewModel.currentScreen() is Screen.DestinationScreen && uiState.selectedFile != null) {
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .widthIn(max = 220.dp),
                    onClick = onCreateFolder,
                ) {
                    Icon(
                        modifier = Modifier.padding(end = ButtonDefaults.IconSpacing),
                        imageVector = Icons.Default.CreateNewFolder,
                        contentDescription = null
                    )
                    Text("Create New Folder")
                }
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .widthIn(max = 220.dp)
                        .padding(start = 8.dp),
                    onClick = {
                        if (uiState.selectedFile!!.path.isNotBlank()) {
                            if (uiState.selectedOperation == FileOperations.COPY) {
                                sharedFileOperationsViewModel.copyFile(
                                    uiState.selectedFile!!.path,
                                    uiState.operationTargetPath
                                )
                            }
                            if (uiState.selectedOperation == FileOperations.MOVE) {
                                sharedFileOperationsViewModel.moveFile(
                                    uiState.selectedFile!!.path,
                                    uiState.operationTargetPath
                                )
                            }
                            onConfirmLocation()
                        }
                    }
                ) {
                    Text(buttonLabel)
                }
            }
        }
    }
}

@Preview
@Composable
fun FabPreview() {
    FileOperationBottomAppBar(
        onCreateFolder = {},
        onConfirmLocation = {},
    )
}
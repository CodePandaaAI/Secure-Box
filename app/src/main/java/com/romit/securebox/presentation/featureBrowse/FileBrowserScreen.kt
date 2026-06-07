package com.romit.securebox.presentation.featureBrowse

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.romit.securebox.R
import com.romit.securebox.components.file.FileCard
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.presentation.sharedViewmodel.SharedFileOperationsUiEvent
import com.romit.securebox.presentation.sharedViewmodel.SharedFileOperationsViewModel
import com.romit.securebox.util.getListItemShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileBrowserScreen(
    modifier: Modifier = Modifier,
    path: String,
    onFileClicked: (FileItem) -> Unit,
) {
    val activity = LocalActivity.current as ComponentActivity
    val fileBrowserScreenViewModel: FileBrowserScreenViewModel = hiltViewModel()
    val sharedFileOperationsViewModel = hiltViewModel<SharedFileOperationsViewModel>(viewModelStoreOwner = activity)
    val uiState by fileBrowserScreenViewModel.uiState.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        sharedFileOperationsViewModel.uiEvents.collect { event ->
            if (event is SharedFileOperationsUiEvent.ShowSnackBar) snackBarHostState.showSnackbar(
                event.message
            )
        }
    }

    LaunchedEffect(path) {
        fileBrowserScreenViewModel.getDirFiles(path)
    }

    when (val state = uiState) {
        is FileBrowserUiState.Loading -> {
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

        is FileBrowserUiState.Success -> {
            if (state.browsingPathDirectories.isNotEmpty()) {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surfaceContainer),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    itemsIndexed(
                        items = state.browsingPathDirectories,
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
                                totalItems = state.browsingPathDirectories.size
                            )
                        )
                    }
                }
            }

        }

        is FileBrowserUiState.Error -> {
            Column(
                modifier = modifier
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
                Text("Nothing Here")
            }
        }
    }
}
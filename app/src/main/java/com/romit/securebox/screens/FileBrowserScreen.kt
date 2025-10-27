package com.romit.securebox.screens

import android.R.attr.path
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.romit.securebox.components.FileCard
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.viewmodels.FileBrowserScreenViewModel

@Composable
fun FileBrowserScreen(
    modifier: Modifier = Modifier,
    path: String,
    onFileClicked: (FileItem) -> Unit,
    viewmodel: FileBrowserScreenViewModel = hiltViewModel()
) {
    val uiState by viewmodel.uiState.collectAsState()

    LaunchedEffect(path) {
        viewmodel.getDirFiles(path)
    }

    if (uiState.isLoading) {
        CircularProgressIndicator()
    } else {
        LazyColumn(contentPadding = PaddingValues(8.dp)) {
            items(uiState.dirFiles, key = { file -> file.path }) { file ->
                FileCard(file = file) { fileItem -> onFileClicked(fileItem) }
            }
        }
    }
}
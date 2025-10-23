package com.romit.securebox.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.romit.securebox.components.FileCard
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.util.StorageHelper.formatDate
import com.romit.securebox.util.StorageHelper.formatFileSize
import com.romit.securebox.util.StorageHelper.getFileIcon
import com.romit.securebox.viewmodels.FileBrowserScreenViewModel

@Composable
fun FileBrowserScreen(
    modifier: Modifier = Modifier,
    path: String,
    viewmodel: FileBrowserScreenViewModel = viewModel()
) {
    val uiState by viewmodel.uiState.collectAsState()

    LaunchedEffect(path) {
        viewmodel.getDirFiles(path)
    }

    if (uiState.isLoading) {
        CircularProgressIndicator()
    } else {
        LazyColumn(contentPadding = PaddingValues(8.dp)) {
            items(uiState.dirFiles) { file ->
                FileCard(file = file) { }
            }
        }
    }
}
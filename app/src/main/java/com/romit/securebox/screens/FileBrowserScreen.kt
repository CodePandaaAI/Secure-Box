package com.romit.securebox.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.romit.securebox.R
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
    } else if (uiState.dirFiles.isNotEmpty()) {
        LazyColumn(contentPadding = PaddingValues(8.dp)) {
            items(uiState.dirFiles, key = { file -> file.path }) { file ->
                FileCard(file = file) { fileItem -> onFileClicked(fileItem) }
            }
        }
    } else {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.empty),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(
                        CircleShape
                    ),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(4.dp))
            Text("Empty")
        }
    }
}
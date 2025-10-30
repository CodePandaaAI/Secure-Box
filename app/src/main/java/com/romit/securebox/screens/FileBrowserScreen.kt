package com.romit.securebox.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import com.romit.securebox.components.FileCard
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.viewmodels.FileBrowserScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileBrowserScreen(
    modifier: Modifier = Modifier,
    path: String,
    onFileClicked: (FileItem) -> Unit,
    viewmodel: FileBrowserScreenViewModel
) {
    val uiState by viewmodel.uiState.collectAsState()

    LaunchedEffect(path) {
        viewmodel.getDirFiles(path)
    }

    when {
        uiState.isLoading -> {
            CircularProgressIndicator()
        }

        uiState.dirFiles.isNotEmpty() -> {
            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                items(uiState.dirFiles, key = { file -> file.path }) { file ->
                    FileCard(
                        file = file,
                        onFileClick = { file -> onFileClicked(file) },
                        onFileOperation = { fileItem ->
                            viewmodel.selectedFileForBottomSheet(fileItem)
                        }
                    )
                }
            }
        }

        else -> {
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
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp))
                Text("Empty")
            }
        }
    }
    if (uiState.selectedFile != null) {
        ModalBottomSheet(onDismissRequest = {
            viewmodel.selectedFileForBottomSheet(null)
        }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = uiState.selectedFile!!.name,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                HorizontalDivider()

                ListItem(
                    headlineContent = { Text("Rename") },
                    leadingContent = { Icon(Icons.Default.Edit, null) },
                    modifier = Modifier.clickable {
                        viewmodel.selectedFileForBottomSheet(null)
                    }
                )

                ListItem(
                    headlineContent = { Text("Delete") },
                    leadingContent = { Icon(Icons.Default.Delete, null) },
                    modifier = Modifier.clickable {
                        viewmodel.selectedFileForBottomSheet(null)
                    }
                )
            }
        }
    }
}
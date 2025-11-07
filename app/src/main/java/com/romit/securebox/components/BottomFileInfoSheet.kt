package com.romit.securebox.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.util.StorageHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomFileInfoSheet(
    onDismiss: (FileItem?) -> Unit,
    selectedFile: () -> FileItem,
    onOpenDeleteDialog: () -> Unit,
    onOpenRenameDialog: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss(null) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Preview
            when {
                selectedFile().isImage -> {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        AsyncImage(
                            model = selectedFile().path,
                            contentDescription = selectedFile().name,
                            modifier = Modifier
                                .size(180.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                selectedFile().isDirectory -> {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Folder,
                            contentDescription = "Folder",
                            modifier = Modifier
                                .padding(24.dp)
                                .size(72.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                else -> {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = StorageHelper.getFileIcon(
                                selectedFile().mimeType,
                                selectedFile().isDirectory
                            ),
                            contentDescription = "File",
                            modifier = Modifier
                                .padding(24.dp)
                                .size(72.dp)
                        )
                    }
                }
            }

            Text(
                text = selectedFile().name,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleLarge
            )

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            ListItem(
                headlineContent = { Text("Rename") },
                leadingContent = { Icon(Icons.Default.Edit, null) },
                modifier = Modifier.clickable {
                    onOpenRenameDialog()
                }
            )

            ListItem(
                headlineContent = { Text("Delete") },
                leadingContent = { Icon(Icons.Default.Delete, null) },
                modifier = Modifier.clickable {
                    onOpenDeleteDialog()
                }
            )
        }
    }
}
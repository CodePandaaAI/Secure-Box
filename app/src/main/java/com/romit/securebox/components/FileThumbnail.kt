package com.romit.securebox.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.ui.theme.SecureBoxTheme


@Composable
fun FileThumbnail(
    file: FileItem,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    when {
        // ✅ If it's an image, show thumbnail

        file.isImage -> {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                AsyncImage(
                    model = file.path,
                    contentDescription = file.name,
                    modifier = modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentScale = ContentScale.Crop,
                    // Show placeholder while loading
                    onLoading = {
                        // Optional: show loading indicator
                    }
                )
            }
        }

        // If it's a folder, show folder icon
        file.isDirectory -> {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Folder,
                    contentDescription = "Folder",
                    modifier = modifier
                        .padding(8.dp)
                )
            }
        }

        // For other allRecents, show generic file icon
        else -> {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "File",
                    modifier = modifier
                        .padding(8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FileThumbnailPreview() {
    SecureBoxTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image Preview
            FileThumbnail(
                file = FileItem(
                    path = "path/to/image.jpg",
                    name = "image.jpg",
                    isDirectory = false,
                    size = "1.2 MB",
                    lastModified = 0L,
                    mimeType = "image/jpeg",
                    isImage = true
                ),
                icon = Icons.Default.Image,
                modifier = Modifier.size(64.dp)
            )

            // Folder Preview
            FileThumbnail(
                file = FileItem(
                    path = "path/to/folder",
                    name = "My Folder",
                    isDirectory = true,
                    size = "0 B",
                    lastModified = 0L,
                    mimeType = null,
                    isImage = false
                ),
                icon = Icons.Default.Folder,
                modifier = Modifier.size(64.dp)
            )

            // Generic File Preview
            FileThumbnail(
                file = FileItem(
                    path = "path/to/document.pdf",
                    name = "document.pdf",
                    isDirectory = false,
                    size = "500 KB",
                    lastModified = 0L,
                    mimeType = "application/pdf",
                    isImage = false
                ),
                icon = Icons.Default.Description,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

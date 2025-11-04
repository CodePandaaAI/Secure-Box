package com.romit.securebox.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.romit.securebox.data.model.FileItem


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
                    modifier = Modifier
                        .padding(16.dp)
                        .size(32.dp)
                )
            }
        }

        // For other files, show generic file icon
        else -> {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "File",
                    modifier = Modifier
                        .padding(16.dp)
                        .size(32.dp)
                )
            }
        }
    }
}
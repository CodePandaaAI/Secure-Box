package com.romit.securebox.components.file

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.util.StorageHelper.formatDate
import com.romit.securebox.util.StorageHelper.getFileIcon

@Composable
fun FileCard(
    file: FileItem,
    onOpenFile: (FileItem) -> Unit,
    onSelectFileForBottomSheet: (FileItem) -> Unit,
    shape: RoundedCornerShape,
) {
    val icon = remember(file.mimeType, file.isDirectory) {
        getFileIcon(file.mimeType, file.isDirectory)
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = shape,
        modifier = Modifier
            .clip(shape = shape)
            .fillMaxWidth()
            .combinedClickable(  // Replace Surface's onClick with this
                onClick = { onOpenFile(file) },
                onLongClick = { onSelectFileForBottomSheet(file) }
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FileThumbnail(file = file, icon = icon, Modifier.size(64.dp))

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${file.size} • ${formatDate(file.lastModified)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { onSelectFileForBottomSheet(file) }
            ) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
            }
        }
    }
}

@Composable
fun RecentsFileSection(
    files: List<FileItem>,
    onOpenFile: (FileItem) -> Unit,
    onSelectFileForBottomSheet: (FileItem) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            files.chunked(3).forEach { rowFiles ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    rowFiles.forEach { item ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .combinedClickable(  // Replace Surface's onClick with this
                                    onClick = { onOpenFile(item) },
                                    onLongClick = { onSelectFileForBottomSheet(item) }
                                ),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            HomeScreenFileThumbnail(
                                file = item,
                                icon = getFileIcon(
                                    item.mimeType,
                                    item.isDirectory
                                ),
                                modifier = Modifier
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun HomeScreenFileThumbnail(
    file: FileItem,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    when {
        // If it's an image, show thumbnail

        file.isImage -> {
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
            Box(
                modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    file.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // If it's a folder, show folder icon
        file.isDirectory -> {
            Icon(
                imageVector = Icons.Filled.Folder,
                contentDescription = "Folder",
                modifier = modifier
                    .padding(8.dp)
            )
            Box(
                modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    file.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // For other allRecents, show generic file icon
        else -> {
            Icon(
                imageVector = icon,
                contentDescription = "File",
                modifier = modifier
                    .padding(16.dp)
            )
            Box(
                modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    file.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

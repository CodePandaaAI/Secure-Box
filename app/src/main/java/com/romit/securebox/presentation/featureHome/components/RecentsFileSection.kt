package com.romit.securebox.presentation.featureHome.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.romit.securebox.components.file.FileIconImage
import com.romit.securebox.components.file.FolderIconImage
import com.romit.securebox.data.model.FileItem

@Composable
fun RecentsFileSection(
    files: List<FileItem>,
    onOpenFile: (FileItem) -> Unit,
    onSelectFileForBottomSheet: (FileItem) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 4.dp,
            bottomStart = 24.dp,
            bottomEnd = 24.dp
        ),
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
                            contentAlignment = Alignment.Center
                        ) {
                            HomeScreenFileThumbnail(
                                file = item,
                                modifier = Modifier.align(Alignment.BottomCenter)
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
    modifier: Modifier = Modifier
) {
    when {
        // If it's an image, show thumbnail
        file.isImage -> {
            AsyncImage(
                model = file.path,
                contentDescription = file.name,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentScale = ContentScale.Crop,
                // Show placeholder while loading
                onLoading = {
                    // Optional: show loading indicator
                }
            )
            Text(
                file.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                modifier = modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)).padding(horizontal = 8.dp),
                overflow = TextOverflow.Ellipsis
            )
        }

        // If it's a folder, show folder icon
        file.isDirectory -> {
            FolderIconImage(
                modifier = Modifier.size(56.dp),
                contentDescription = "Folder"
            )
            Text(
                file.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                modifier = modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)).padding(horizontal = 8.dp),
                overflow = TextOverflow.Ellipsis
            )
        }

        // For other allRecents, show generic file icon
        else -> {
            FileIconImage(
                file = file,
                contentDescription = "File",
                modifier = Modifier.size(56.dp)
            )
            Text(
                file.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                modifier = modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)).padding(horizontal = 8.dp),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

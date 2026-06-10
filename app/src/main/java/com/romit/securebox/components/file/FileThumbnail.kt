package com.romit.securebox.components.file

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.romit.securebox.R
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.ui.theme.SecureBoxTheme
import com.romit.securebox.util.StorageHelper

@Composable
fun FileThumbnail(
    file: FileItem,
    modifier: Modifier = Modifier
) {
    when {
        file.isImage -> {
            Box(
                modifier = modifier.clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = file.path,
                    contentDescription = file.name,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop,
                    onLoading = {}
                )
            }
        }

        file.isDirectory -> {
            FolderIconImage(
                contentDescription = "Folder",
                modifier = modifier
            )
        }

        else -> {
            FileIconImage(
                file = file,
                modifier = modifier
            )
        }
    }
}

@Composable
fun FolderIconImage(
    modifier: Modifier = Modifier,
    contentDescription: String? = "Folder"
) {
    Image(
        painter = painterResource(R.drawable.app_folder_icon),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Composable
fun FileIconImage(
    file: FileItem,
    modifier: Modifier = Modifier,
    contentDescription: String = "File"
) {
    Image(
        painter = painterResource(StorageHelper.getFileIconRes(file)),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Preview(showBackground = true)
@Composable
fun FileThumbnailPreview() {
    SecureBoxTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                modifier = Modifier.size(64.dp)
            )

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
                modifier = Modifier.size(64.dp)
            )

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
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

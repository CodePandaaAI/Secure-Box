package com.romit.securebox.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.util.StorageHelper.formatDate
import com.romit.securebox.util.StorageHelper.formatFileSize
import com.romit.securebox.util.StorageHelper.getFileIcon

@Composable
fun FileCard(
    file: FileItem, onClick: () -> Unit
) {
    val iconAndColor = remember { getFileIcon(file.mimeType, file.isDirectory) }

    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (iconAndColor.second != 0) iconAndColor.second as Color
                else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = getFileIcon(file.mimeType, file.isDirectory).first,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp).size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${formatFileSize(file.size)} • ${formatDate(file.lastModified)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
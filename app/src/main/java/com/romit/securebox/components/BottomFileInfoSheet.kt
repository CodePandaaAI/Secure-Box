package com.romit.securebox.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    onOpenRenameDialog: () -> Unit,
    onCopyTo: (FileItem) -> Unit,
    onMoveTo: (FileItem) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss(null) },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ✅ Preview with better styling
            when {
                selectedFile().isImage -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        AsyncImage(
                            model = selectedFile().path,
                            contentDescription = selectedFile().name,
                            modifier = Modifier
                                .size(128.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                selectedFile().isDirectory -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceContainer else Color.Gray.copy(
                                alpha = 0.2f
                            )
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(128.dp)
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Folder,
                                contentDescription = "Folder",
                                modifier = Modifier.fillMaxSize(),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                else -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceContainer else Color.Gray.copy(
                                alpha = 0.2f
                            )
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(128.dp)
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = StorageHelper.getFileIcon(
                                    selectedFile().mimeType,
                                    selectedFile().isDirectory
                                ),
                                contentDescription = "File",
                                modifier = Modifier.fillMaxSize(),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            // ✅ File name with better typography
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = selectedFile().name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Text(
                    text = "${selectedFile().size} • ${StorageHelper.formatDate(selectedFile().lastModified)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // ✅ Action buttons with better styling
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Rename button
                Surface(
                    onClick = {
                        onOpenRenameDialog()
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceContainer else Color.Gray.copy(
                        alpha = 0.2f
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Rename",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Delete button
                Surface(
                    onClick = {
                        onOpenDeleteDialog()
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceContainer else Color.Gray.copy(
                        alpha = 0.2f
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Delete",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // CopyTo
                Surface(
                    onClick = {
                        onCopyTo(selectedFile())
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceContainer else Color.Gray.copy(
                        alpha = 0.2f
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "CopyTo",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // MoveTo
                Surface(
                    onClick = {
                        onMoveTo(selectedFile())
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceContainer else Color.Gray.copy(
                        alpha = 0.2f
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "MoveTo",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
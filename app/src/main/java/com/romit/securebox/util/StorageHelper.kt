package com.romit.securebox.util

import android.os.Environment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.romit.securebox.data.model.StorageCategory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log10

object StorageHelper {
    fun getStorageCategories(): List<StorageCategory> {
        val downloadsPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val dcimPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
        val moviesPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath
        val musicPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath
        val documentsPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
        val internalPath = Environment.getExternalStorageDirectory().absolutePath

        return listOf(
            StorageCategory(
                name = "Downloads",
                description = "Downloaded Files",
                path = downloadsPath,
                icon = Icons.Default.Download
            ),
            StorageCategory(
                name = "Images",
                description = "All Images",
                path = dcimPath,
                icon = Icons.Default.PermMedia
            ),
            StorageCategory(
                name = "Videos",
                description = "Video files",
                path = moviesPath,
                icon = Icons.Default.VideoLibrary
            ),
            StorageCategory(
                name = "Music",
                description = "Audio files",
                path = musicPath,
                icon = Icons.Default.MusicNote
            ),
            StorageCategory(
                name = "Documents",
                description = "Documents and files",
                path = documentsPath,
                icon = Icons.Default.Description
            ),
            StorageCategory(
                name = "Internal Storage",
                description = "Browse all files",
                path = internalPath,
                icon = Icons.Default.Storage
            )
        )
    }

    fun getFileIcon(mimeType: String?, isDirectory: Boolean): Pair<ImageVector, Any> {
        if (isDirectory) return Pair(Icons.Default.Folder, 0)

        // Pick icon based on MIME type
        return when {
            mimeType == null -> Pair(Icons.AutoMirrored.Filled.InsertDriveFile, 0)
            mimeType.startsWith("image/") -> Pair(Icons.Default.Image, Color(0xFF3D92E7))
            mimeType.startsWith("video/") -> Pair(Icons.Default.VideoFile, Color(0xFF987BE1))
            mimeType.startsWith("audio/") -> Pair(Icons.Default.MusicNote, Color(0xFFD9A04A))
            mimeType == "application/pdf" -> Pair(Icons.Default.PictureAsPdf, Color(0xFFF17346))
            mimeType.startsWith("text/") -> Pair(Icons.Default.Description, Color(0xFF202020))
            mimeType.contains("zip") -> Pair(Icons.Default.FolderZip, Color(0xFF24B2A2))
            else -> Pair(Icons.AutoMirrored.Filled.InsertDriveFile, 0)
        }
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"

        val kb = bytes / 1024.0
        if (kb < 1024) return "%.1f KB".format(kb)

        val mb = kb / 1024.0
        if (mb < 1024) return "%.1f MB".format(mb)

        val gb = mb / 1024.0
        return "%.2f GB".format(gb)
    }

    fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val now = Date()

        val diffInMillis = now.time - date.time
        val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)

        return when {
            diffInDays == 0L -> {
                // Today: show time only
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            }

            diffInDays == 1L -> "Yesterday"
            diffInDays < 7 -> SimpleDateFormat(
                "EEEE",
                Locale.getDefault()
            ).format(date) // Day-of-week
            else -> SimpleDateFormat(
                "MMM dd, yyyy",
                Locale.getDefault()
            ).format(date)   // Full date
        }
    }

    fun getMimeType(file: File): String? {
        val extension = file.extension
        return android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
}
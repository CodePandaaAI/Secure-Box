package com.romit.securebox.util

import android.os.Environment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.romit.securebox.data.model.StorageCategory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

object StorageHelper {
    fun getStorageCategories(): List<StorageCategory> {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val dcimDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val moviesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val musicDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        val documentsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val internalDir = Environment.getExternalStorageDirectory()

        return listOf(
            StorageCategory(
                name = "Downloads",
                path = downloadsDir.absolutePath,
                icon = Icons.Outlined.Download
            ),
            StorageCategory(
                name = "Images",
                path = dcimDir.absolutePath,
                icon = Icons.Outlined.Image
            ),
            StorageCategory(
                name = "Videos",
                path = moviesDir.absolutePath,
                icon = Icons.Outlined.VideoLibrary
            ),
            StorageCategory(
                name = "Music",
                path = musicDir.absolutePath,
                icon = Icons.Outlined.Audiotrack
            ),
            StorageCategory(
                name = "Documents",
                path = documentsDir.absolutePath,
                icon = Icons.AutoMirrored.Outlined.InsertDriveFile
            ),
            StorageCategory(
                name = "Internal Storage",
                path = internalDir.absolutePath,
                icon = Icons.Outlined.Storage
            )
        )
    }

    fun getFileIcon(mimeType: String?, isDirectory: Boolean): Pair<ImageVector, Any> {
        if (isDirectory) return Pair(Icons.Default.Folder, 0)

        // Pick icon based on MIME type
        return when {
            mimeType == null -> Pair(Icons.AutoMirrored.Filled.InsertDriveFile, 0)

            // Images
            mimeType.startsWith("image/") -> Pair(Icons.Default.Image, Color(0xFF3D92E7))

            // Videos
            mimeType.startsWith("video/") -> Pair(Icons.Default.VideoFile, Color(0xFF987BE1))

            // Audio
            mimeType.startsWith("audio/") -> Pair(Icons.Default.MusicNote, Color(0xFFD9A04A))

            // Documents
            mimeType == "application/pdf" -> Pair(Icons.Default.PictureAsPdf, Color(0xFFF17346))
            mimeType.startsWith("text/") -> Pair(Icons.Default.Description, Color(0xFF202020))

            // Microsoft Office Documents
            mimeType == "application/msword" ||
                    mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ->
                Pair(Icons.Default.Description, Color(0xFF2B579A)) // Word docs

            mimeType == "application/vnd.ms-excel" ||
                    mimeType == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ->
                Pair(Icons.Default.TableChart, Color(0xFF1D6F42)) // Excel sheets

            mimeType == "application/vnd.ms-powerpoint" ||
                    mimeType == "application/vnd.openxmlformats-officedocument.presentationml.presentation" ->
                Pair(Icons.Default.Slideshow, Color(0xFFD24726)) // PowerPoint presentations

            // Archives
            mimeType.contains("zip") -> Pair(Icons.Default.FolderZip, Color(0xFF24B2A2))
            mimeType == "application/x-rar-compressed" ||
                    mimeType == "application/vnd.rar" -> Pair(
                Icons.Default.FolderZip,
                Color(0xFF24B2A2)
            )

            mimeType == "application/x-7z-compressed" -> Pair(
                Icons.Default.FolderZip,
                Color(0xFF24B2A2)
            )

            mimeType == "application/x-tar" ||
                    mimeType == "application/gzip" -> Pair(
                Icons.Default.FolderZip,
                Color(0xFF24B2A2)
            )

            // Android APK
            mimeType == "application/vnd.android.package-archive" ->
                Pair(Icons.Default.Android, Color(0xFF3DDC84))

            // Code files
            mimeType == "application/json" ||
                    mimeType == "application/javascript" ||
                    mimeType == "application/xml" ||
                    mimeType.startsWith("text/x-") -> Pair(Icons.Default.Code, Color(0xFF616161))

            // Executables
            mimeType == "application/x-msdownload" ||
                    mimeType == "application/x-executable" ->
                Pair(Icons.Default.Settings, Color(0xFF757575))

            else -> Pair(Icons.AutoMirrored.Filled.InsertDriveFile, 0)
        }
    }

    fun getDirectorySize(directory: File): Long {
        var size = 0L
        if (directory.isDirectory) {
            directory.walkTopDown().forEach { file ->
                if (file.isFile) {
                    size += file.length()
                }
            }
        }
        return size
    }

    fun formatSize(bytes: Long): String {
        if (bytes < 0) return "Invalid size"
        if (bytes < 1024) return "$bytes B"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()

        val unitIndex = if (digitGroups < units.size) digitGroups else units.size - 1

        val value = bytes / 1024.0.pow(unitIndex.toDouble())

        return String.format(
            Locale.getDefault(),
            "%.1f %s",
            value,
            units[unitIndex]
        )
    }


    fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val now = Date()

        val differenceInMilliseconds = now.time - date.time
        val differenceInDays = differenceInMilliseconds / (1000 * 60 * 60 * 24)

        return when {
            differenceInDays == 0L -> {
                // Today: show time only
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            }

            differenceInDays == 1L -> "Yesterday"
            differenceInDays < 7 -> SimpleDateFormat(
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

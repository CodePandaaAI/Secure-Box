package com.romit.securebox.util

import android.os.Environment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.VideoLibrary
import com.romit.securebox.R
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.data.model.StorageCategory
import com.romit.securebox.data.model.StorageCategoryType
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
                icon = Icons.Outlined.Download,
                type = StorageCategoryType.DOWNLOADS
            ),
            StorageCategory(
                name = "Images",
                path = dcimDir.absolutePath,
                icon = Icons.Outlined.Image,
                type = StorageCategoryType.IMAGES
            ),
            StorageCategory(
                name = "Videos",
                path = moviesDir.absolutePath,
                icon = Icons.Outlined.VideoLibrary,
                type = StorageCategoryType.VIDEOS
            ),
            StorageCategory(
                name = "Music",
                path = musicDir.absolutePath,
                icon = Icons.Outlined.Audiotrack,
                type = StorageCategoryType.MUSIC
            ),
            StorageCategory(
                name = "Documents",
                path = documentsDir.absolutePath,
                icon = Icons.AutoMirrored.Outlined.InsertDriveFile,
                type = StorageCategoryType.DOCUMENTS
            ),
            StorageCategory(
                name = "Internal Storage",
                path = internalDir.absolutePath,
                icon = Icons.Outlined.Storage,
                type = StorageCategoryType.INTERNAL_STORAGE
            )
        )
    }

    fun getFileIconRes(file: FileItem): Int {
        val extension = (file.extension ?: File(file.path).extension).lowercase()
        val mimeType = file.mimeType.orEmpty()

        return when {
            extension == "pdf" -> R.drawable.app_file_icon_pdf

            extension in wordExtensions ||
                    mimeType == "application/msword" ||
                    mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> {
                R.drawable.app_file_icon_docx
            }

            extension in spreadsheetExtensions ||
                    mimeType == "application/vnd.ms-excel" ||
                    mimeType == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> {
                R.drawable.app_file_icon_xlsx
            }

            extension in presentationExtensions ||
                    mimeType == "application/vnd.ms-powerpoint" ||
                    mimeType == "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> {
                R.drawable.app_file_icon_ppt
            }

            extension in archiveExtensions ||
                    mimeType.contains("zip") ||
                    mimeType == "application/x-rar-compressed" ||
                    mimeType == "application/vnd.rar" ||
                    mimeType == "application/x-7z-compressed" ||
                    mimeType == "application/x-tar" ||
                    mimeType == "application/gzip" -> {
                R.drawable.app_file_icon_zip
            }

            extension in videoExtensions || mimeType.startsWith("video/") -> {
                R.drawable.app_file_icon_video
            }

            extension in kotlinExtensions -> {
                R.drawable.app_file_icon_kotlin
            }

            extension in htmlExtensions -> {
                R.drawable.app_file_icon_html
            }

            extension in codeExtensions ||
                    mimeType == "application/json" ||
                    mimeType == "application/javascript" ||
                    mimeType == "application/xml" ||
                    mimeType.startsWith("text/x-") -> {
                R.drawable.app_file_icon_code
            }

            extension in textExtensions || mimeType.startsWith("text/") -> {
                R.drawable.app_file_icon_txt
            }

            else -> R.drawable.app_file_icon_txt
        }
    }

    private val wordExtensions = setOf("doc", "docx", "odt")
    private val spreadsheetExtensions = setOf("xls", "xlsx", "ods", "csv")
    private val presentationExtensions = setOf("ppt", "pptx", "odp")
    private val archiveExtensions = setOf("zip", "rar", "7z", "tar", "gz", "tgz")
    private val videoExtensions = setOf("mp4", "mkv", "mov", "avi", "webm", "3gp", "m4v")
    private val textExtensions = setOf("txt", "md", "rtf", "log")
    private val kotlinExtensions = setOf("kt", "kts")
    private val htmlExtensions = setOf("html", "htm")
    private val codeExtensions = setOf(
        "java",
        "xml",
        "json",
        "css",
        "js",
        "ts",
        "tsx",
        "jsx",
        "py",
        "c",
        "cpp",
        "h",
        "hpp",
        "cs",
        "go",
        "rs",
        "php",
        "rb",
        "sh",
        "bat",
        "gradle",
        "properties",
        "toml",
        "yaml",
        "yml"
    )

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

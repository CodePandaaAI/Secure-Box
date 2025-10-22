package com.romit.securebox.util

import android.os.Environment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VideoLibrary
import com.romit.securebox.data.model.StorageCategory

object StorageHelper {
    fun getStorageCategories(): List<StorageCategory> {
        val downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
        val moviesPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath
        val musicPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath
        val documentsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
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
}
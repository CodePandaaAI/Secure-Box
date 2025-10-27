package com.romit.securebox.data.repository

import android.os.Environment
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.util.StorageHelper
import com.romit.securebox.util.StorageHelper.getMimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepository @Inject constructor() {
    suspend fun getRecentFiles(limit: Int = 6): List<FileItem> {
        return withContext(Dispatchers.IO) {
            try {
                val downloadDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                if (!downloadDir.exists()) {
                    return@withContext emptyList()
                }

                val files = downloadDir.listFiles()
                    ?.filter { it.isFile }
                    ?.sortedByDescending { it.lastModified() }
                    ?.take(limit)
                    ?.map { file ->
                        FileItem(
                            path = file.absolutePath,
                            name = file.name,
                            isDirectory = file.isDirectory,
                            size = file.length(),
                            lastModified = file.lastModified(),
                            mimeType = getMimeType(file),
                            extension = file.extension
                        )
                    } ?: emptyList()

                files
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun getDirFileItems(path: String): List<FileItem> {
        return withContext(Dispatchers.IO) {
            if (!File(path).exists()) return@withContext emptyList()
            try {
                val files = File(path).listFiles() ?: return@withContext emptyList()

                files.sortedByDescending { it.lastModified() }
                    .map { file ->
                        FileItem(
                            path = file.absolutePath,
                            name = file.name,
                            isDirectory = file.isDirectory,
                            size = file.length(),
                            lastModified = file.lastModified(),
                            mimeType = if (file.isDirectory) null else getMimeType(file),
                            extension = file.extension
                        )
                    }
            } catch (e: Exception) {
                return@withContext emptyList()
            }
        }
    }

    suspend fun getDirectorySize(directory: String): String {
        return withContext(Dispatchers.IO) {
            val file = File(directory)

            if (!file.exists()) return@withContext "0 B"
            if (!file.isDirectory) return@withContext "0 B"

            val files = file.listFiles()
            if (files == null || files.isEmpty()) return@withContext "0 B"

            try {
                val dirSize = file.walkTopDown().sumOf { it.length() }
                StorageHelper.formatFileSize(dirSize)
            } catch (e: Exception) {
                "0 B"
            }
        }
    }

}
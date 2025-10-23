package com.romit.securebox.data.repository

import android.os.Environment
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.util.StorageHelper.getMimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.collections.emptyList

class FileRepository {
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

    suspend fun getDirFiles(path: String): List<File> {
        return withContext(Dispatchers.IO) {
            if (!File(path).exists()) return@withContext emptyList()
            try {
                val files = File(path).listFiles()?.toList()
                return@withContext files!!
            } catch (e: Exception) {
                return@withContext emptyList()
            }
        }
    }
}
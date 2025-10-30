package com.romit.securebox.data.repository

import android.os.Environment
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.util.StorageHelper
import com.romit.securebox.util.StorageHelper.getMimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepository @Inject constructor() {
    suspend fun getRecentFiles(limit: Int): List<FileItem> {
        return withContext(Dispatchers.IO) {
            try {
                val downloadDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                if (!downloadDir.exists()) {
                    return@withContext emptyList()
                }

                val files = downloadDir.listFiles()?.filter { it.isFile }
                    ?.sortedByDescending { it.lastModified() }?.take(limit)?.map { file ->
                        FileItem(
                            path = file.absolutePath,
                            name = file.name,
                            isDirectory = file.isDirectory,
                            size = StorageHelper.formatSize(file.length()),
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

                files.sortedByDescending { it.lastModified() }.map { file ->
                    val size = if (!file.isDirectory) {
                        file.length()
                    } else {
                        0
                    }
                    FileItem(
                        path = file.absolutePath,
                        name = file.name,
                        isDirectory = file.isDirectory,
                        size = StorageHelper.formatSize(size),
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

    suspend fun getDirectorySize(path: String): String {
        return withContext(Dispatchers.IO) {
            val size = StorageHelper.getDirectorySize(File(path))
            StorageHelper.formatSize(size)
        }
    }

    suspend fun deleteFile(filePath: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(filePath)

                if (!file.exists()) {
                    return@withContext Result.failure(
                        FileNotFoundException("File not found")
                    )
                }

                val success = if (file.isDirectory) {
                    file.deleteRecursively()
                } else {
                    file.delete()
                }

                if (success) {
                    Result.success("Deleted successfully")
                } else {
                    Result.failure(IOException("Delete failed. Check permissions or storage."))
                }

            } catch (e: SecurityException) {
                Result.failure(SecurityException("Permission denied"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
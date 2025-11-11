package com.romit.securebox.data.repository

import android.app.Application
import android.content.ContentResolver
import android.os.Bundle
import android.provider.MediaStore
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
class FileRepository @Inject constructor(application: Application) {

    private val contentResolver = application.contentResolver
    suspend fun getRecentFiles(limit: Int): List<FileItem> {
        return getRecentFiles(null, pageSize = limit)
    }

    suspend fun getRecentFiles(lastTimestamp: Long?, pageSize: Int): List<FileItem> {
        return withContext(Dispatchers.IO) {
            val files = mutableListOf<FileItem>()

            // 1. Define what columns we want to get
            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA, // The file path
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.MIME_TYPE
            )

            // 2. Define how to sort
            val sortOrder = "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"

            // 3. Define Selection
            val selection = if (lastTimestamp != null) {
                // If we have a timestamp, find files OLDER than it
                "${MediaStore.Files.FileColumns.MIME_TYPE} IS NOT NULL" +
                        " AND ${MediaStore.Files.FileColumns.DATE_MODIFIED} * 1000 < $lastTimestamp"
            } else {
                // First load, just get the newest
                "${MediaStore.Files.FileColumns.MIME_TYPE} IS NOT NULL"
            }

            // 4. Define the query arguments for pagination
            val queryArgs = Bundle().apply {
                putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                // Sort order
                putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, sortOrder)
                // Page size (LIMIT)
                putInt(ContentResolver.QUERY_ARG_LIMIT, pageSize)
            }

            // 5. Execute the query
            val cursor = contentResolver.query(
                MediaStore.Files.getContentUri("external"), // The "table" to query
                projection, // The columns to get
                queryArgs,  // The pagination and filtering
                null
            )

            // 6. Loop through the results (the cursor)
            cursor?.use {
                val pathColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                val modifiedColumn =
                    it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
                val mimeTypeColumn =
                    it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)

                while (it.moveToNext()) {
                    val path = it.getString(pathColumn)
                    val file = File(path)

                    // MediaStore can be slow to update, skip if file was deleted
                    if (!file.exists()) continue

                    val name = it.getString(nameColumn)
                    val size = it.getLong(sizeColumn)
                    // MediaStore timestamp is in SECONDS, we need Milliseconds
                    val modified = it.getLong(modifiedColumn) * 1000L
                    val mimeType = it.getString(mimeTypeColumn)

                    // Use the mimeType to check if it's an image
                    val isImage = mimeType?.startsWith("image/") == true

                    files.add(
                        FileItem(
                            path = path,
                            name = name,
                            isDirectory = false, // We filtered out directories
                            size = StorageHelper.formatSize(size), // Use your helper
                            lastModified = modified,
                            mimeType = mimeType,
                            extension = file.extension, // Get extension from file
                            isImage = isImage
                        )
                    )
                }
            }
            files // Return the list
        }
    }

    suspend fun copyFile(filePath: String, destPath: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val sourceFile = File(filePath)

                if (!sourceFile.exists()) {
                    return@withContext Result.failure(FileNotFoundException("File Not Found"))
                }

                val destFile = File(destPath, sourceFile.name)

                if (destFile.exists()) {
                    return@withContext Result.failure(
                        FileAlreadyExistsException(
                            file = destFile,
                            reason = "File Already Exists"
                        )
                    )
                }

                if (sourceFile.isDirectory) {
                    sourceFile.copyRecursively(destFile, false)
                } else sourceFile.copyTo(destFile, false)

                Result.success("File Copied Successfully!")
            } catch (e: SecurityException) {
                Result.failure(e)
            } catch (e: IOException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun moveTo(sourcePath: String, destinationPath: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val sourceFile = File(sourcePath)
                val destDir = File(destinationPath)

                // Validation
                if (!sourceFile.exists()) {
                    return@withContext Result.failure(
                        FileNotFoundException("Source file not found")
                    )
                }

                if (!destDir.exists() || !destDir.isDirectory) {
                    return@withContext Result.failure(
                        FileNotFoundException("Destination directory not found")
                    )
                }

                val destFile = File(destDir, sourceFile.name)

                if (destFile.exists()) {
                    return@withContext Result.failure(
                        FileAlreadyExistsException(
                            file = destFile,
                            reason = "File already exists at destination"
                        )
                    )
                }

                // Permission checks
                val sourceParent = sourceFile.parentFile
                if (sourceParent?.canWrite() == false) {
                    return@withContext Result.failure(
                        SecurityException("No write permission in source directory")
                    )
                }

                if (!destDir.canWrite()) {
                    return@withContext Result.failure(
                        SecurityException("No write permission in destination directory")
                    )
                }

                // Storage space check
                val sourceSize = if (sourceFile.isDirectory) {
                    sourceFile.walkTopDown()
                        .filter { it.isFile }
                        .sumOf { it.length() }
                } else {
                    sourceFile.length()
                }

                val availableSpace = destDir.usableSpace

                if (sourceSize > availableSpace) {
                    return@withContext Result.failure(
                        IOException(
                            "Insufficient storage space. " +
                                    "Need ${sourceSize / 1_048_576}MB, " +
                                    "available ${availableSpace / 1_048_576}MB"
                        )
                    )
                }

                // Try fast path first (same partition)
                val renamed = sourceFile.renameTo(destFile)

                if (renamed) {
                    return@withContext Result.success("Moved successfully")
                }

                // Fallback: cross-partition move (copy + delete)
                try {
                    if (sourceFile.isDirectory) {
                        sourceFile.copyRecursively(destFile, overwrite = false)
                    } else {
                        sourceFile.copyTo(destFile, overwrite = false)
                    }
                } catch (e: Exception) {
                    // Copy failed - ensure no partial copy remains
                    if (destFile.exists()) {
                        try {
                            if (destFile.isDirectory) {
                                destFile.deleteRecursively()
                            } else {
                                destFile.delete()
                            }
                        } catch (cleanupException: Exception) {
                        }
                    }
                    throw e
                }

                // Delete source after successful copy
                val deleted = if (sourceFile.isDirectory) {
                    sourceFile.deleteRecursively()
                } else {
                    sourceFile.delete()
                }

                if (deleted) {
                    Result.success("Moved successfully")
                } else {
                    // Copy succeeded but delete failed - user needs to know
                    Result.failure(
                        IOException(
                            "File copied to ${destFile.absolutePath} but failed to delete original at ${sourceFile.absolutePath}. " +
                                    "You may need to manually delete the source file."
                        )
                    )
                }

            } catch (e: FileNotFoundException) {
                Result.failure(e)
            } catch (e: FileAlreadyExistsException) {
                Result.failure(e)
            } catch (e: SecurityException) {
                Result.failure(e)
            } catch (e: IOException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(IOException("Unexpected error during move: ${e.message}"))
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
                        0L
                    }

                    val extension = file.extension

                    val isImage =
                        extension.lowercase() in listOf("jpg", "jpeg", "png", "webp", "bmp", "gif")

                    FileItem(
                        path = file.absolutePath,
                        name = file.name,
                        isDirectory = file.isDirectory,
                        size = StorageHelper.formatSize(size),
                        lastModified = file.lastModified(),
                        mimeType = if (file.isDirectory) null else getMimeType(file),
                        extension = file.extension,
                        isImage = isImage
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

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun renameFile(filePath: String, newName: String): Result<String> {  // ✅ Changed name
        return withContext(Dispatchers.IO) {
            try {
                val oldFile = File(filePath)

                if (!oldFile.exists()) {
                    return@withContext Result.failure(
                        FileNotFoundException("File not found")
                    )
                }

                if (newName.isBlank()) {
                    return@withContext Result.failure(
                        IllegalArgumentException("Name cannot be empty")
                    )
                }

                val invalidCharacters = setOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')
                if (newName.any { it in invalidCharacters }) {
                    return@withContext Result.failure(
                        IllegalArgumentException("Name cannot contain invalid characters (e.g., /, \\, :, *, ?, \", <, >, |)")
                    )
                }

                val parentPath = oldFile.parent
                    ?: return@withContext Result.failure(
                        IllegalStateException("Cannot access parent directory")
                    )

                val newFile = File(parentPath, newName)

                if (newFile.exists()) {
                    return@withContext Result.failure(
                        FileAlreadyExistsException(
                            file = newFile,
                            reason = "A file with that name already exists"
                        )
                    )
                }

                val success = oldFile.renameTo(newFile)

                if (success) {
                    Result.success("Renamed successfully")
                } else {
                    Result.failure(IOException("Rename failed. Check permissions."))
                }

            } catch (e: SecurityException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
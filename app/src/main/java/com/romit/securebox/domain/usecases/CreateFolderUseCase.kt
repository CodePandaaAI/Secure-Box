package com.romit.securebox.domain.usecases

import com.romit.securebox.data.repository.FileRepository
import jakarta.inject.Inject
import java.io.FileNotFoundException

class CreateFolderUseCase @Inject constructor(private val fileRepository: FileRepository) {
    suspend operator fun invoke(parentPath: String, folderName: String): Result<String> {
        return fileRepository.createFolder(parentPath, folderName).fold(
            onSuccess = { message ->
                Result.success(message)
            },
            onFailure = { exception ->
                val errorMessage = when (exception) {
                    is FileNotFoundException -> "Parent directory not found"
                    is SecurityException -> "Permission denied"
                    is IllegalArgumentException -> exception.message ?: "Invalid folder name"
                    is FileAlreadyExistsException -> "Folder already exists"
                    else -> "Failed to create folder: ${exception.message}"
                }
                Result.failure(Exception(errorMessage))
            }
        )
    }
}
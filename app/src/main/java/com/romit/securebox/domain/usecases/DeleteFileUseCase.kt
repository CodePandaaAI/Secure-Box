package com.romit.securebox.domain.usecases

import com.romit.securebox.data.repository.FileRepository
import jakarta.inject.Inject
import java.io.FileNotFoundException
import java.io.IOException

class DeleteFileUseCase @Inject constructor(private val fileRepository: FileRepository) {
    suspend operator fun invoke(filePath: String): Result<String> {
        return fileRepository.deleteFile(filePath).fold(
            onSuccess = { message ->
                Result.success(message)
            },
            onFailure = { exception ->
                val errorMessage = when (exception) {
                    is FileNotFoundException -> "File not found"
                    is SecurityException -> "Permission denied"
                    is IOException -> "Cannot delete file"
                    else -> exception.message ?: "Failed to delete"
                }
                Result.failure(Exception(errorMessage))
            }
        )
    }
}
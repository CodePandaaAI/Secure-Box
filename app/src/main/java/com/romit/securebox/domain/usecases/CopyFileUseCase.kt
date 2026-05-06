package com.romit.securebox.domain.usecases

import com.romit.securebox.data.repository.FileRepository
import jakarta.inject.Inject
import java.io.FileNotFoundException
import java.io.IOException

class CopyFileUseCase @Inject constructor(private val fileRepository: FileRepository) {
    suspend operator fun invoke(filePath: String, destPath: String): Result<String> {
        return fileRepository.copyFile(filePath, destPath).fold(
            onSuccess = { message ->
                Result.success(message)
            },
            onFailure = { message ->
                val error = when (message) {
                    is FileNotFoundException -> "File not found"
                    is FileAlreadyExistsException -> "File already exists"
                    is SecurityException -> "Permission denied"
                    is IOException -> "Copy failed"
                    else -> message.message ?: "Unknown errorMessage"
                }
                Result.failure(Exception(error))
            }
        )
    }
}
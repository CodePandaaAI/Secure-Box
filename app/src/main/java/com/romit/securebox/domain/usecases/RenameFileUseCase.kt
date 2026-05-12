package com.romit.securebox.domain.usecases

import com.romit.securebox.data.repository.FileRepository
import jakarta.inject.Inject
import java.io.FileNotFoundException
import java.io.IOException

class RenameFileUseCase @Inject constructor(private val fileRepository: FileRepository) {
    suspend operator fun invoke(filePath: String, newName: String): Result<String> {
        return fileRepository.renameFile(filePath, newName).fold(
            onSuccess = { message ->
               Result.success(message)
            },
            onFailure = { exception ->
                val errorMessage = when (exception) {
                    is FileNotFoundException -> "File not found"
                    is IllegalArgumentException -> "Invalid name"
                    is FileAlreadyExistsException -> "Name already exists"
                    is IOException -> "Rename failed"
                    is SecurityException -> "Permission denied"
                    else -> exception.message ?: "Unknown errorMessage"
                }
                Result.failure(Exception(errorMessage))
            }
        )
    }
}
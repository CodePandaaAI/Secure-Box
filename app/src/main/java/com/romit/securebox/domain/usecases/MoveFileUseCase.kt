package com.romit.securebox.domain.usecases

import com.romit.securebox.data.repository.FileRepository
import jakarta.inject.Inject

class MoveFileUseCase @Inject constructor(private val fileRepository: FileRepository) {
    suspend operator fun invoke(filePath: String, destPath: String): Result<String> {
        return fileRepository.moveTo(filePath, destPath).fold(
            onSuccess = { message ->
                Result.success(message)
            },
            onFailure = { throwable ->
                Result.failure(throwable)
            }
        )
    }
}
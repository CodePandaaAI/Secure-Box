package com.romit.securebox.domain.usecases

import com.romit.securebox.data.model.FileItem
import com.romit.securebox.data.repository.FileRepository
import jakarta.inject.Inject

class GetDirectoriesUseCase @Inject constructor(private val fileRepository: FileRepository) {
    suspend operator fun invoke(dirPath: String): Result<List<FileItem>> {
        return fileRepository.getDirs(path = dirPath).fold(
            onSuccess = { directories ->
                Result.success(directories)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }
}
package com.romit.securebox.presentation.featureMedia

import com.romit.securebox.data.model.FileItem

sealed interface MediaFilesUiState {
    data object Loading : MediaFilesUiState

    data class Success(
        val files: List<FileItem> = emptyList(),
        val isPaginationEndReached: Boolean = false,
        val isLoadingNextPage: Boolean = false
    ) : MediaFilesUiState

    data class Error(val message: String) : MediaFilesUiState
}

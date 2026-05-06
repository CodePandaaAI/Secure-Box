package com.romit.securebox.presentation.featureRecents

import com.romit.securebox.data.model.FileItem

sealed interface AllRecentsUiState {
    data object Loading : AllRecentsUiState

    data class Success(
        val allRecents: List<FileItem> = emptyList(),
        val isPaginationEndReached: Boolean = false,
        val isLoadingNextPage: Boolean = false,
    ) : AllRecentsUiState

    data class Error(val message: String): AllRecentsUiState
}
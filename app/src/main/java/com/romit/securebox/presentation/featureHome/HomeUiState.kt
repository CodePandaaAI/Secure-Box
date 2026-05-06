package com.romit.securebox.presentation.featureHome

import com.romit.securebox.data.model.FileItem

sealed interface HomeUiState {
    data object RecentFilesLoading : HomeUiState

    data class Success(
        val recentFilesList: List<FileItem> = emptyList()
    ) : HomeUiState

    data class Error(val message: String): HomeUiState
}
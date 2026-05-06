package com.romit.securebox.presentation.featureBrowse

import com.romit.securebox.data.model.FileItem

sealed interface FileBrowserUiState {
    data object Loading: FileBrowserUiState

    data class Success(
        val browsingPath: String = "", // Current Path for browsing folders accessing allRecents/renaming/deleting/copying/Creating new folders and so on (For Navigation and general purpose work)
        val browsingPathDirectories: List<FileItem> = emptyList(), // Items(Dir) inside browsing path
    ): FileBrowserUiState

    data class Error(val message: String): FileBrowserUiState
}
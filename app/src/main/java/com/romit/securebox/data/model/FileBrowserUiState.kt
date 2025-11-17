package com.romit.securebox.data.model

import android.os.Environment

data class FileBrowserUiState(
    val browsingPath: String = "", // Current Path for browsing folders accessing allRecents/renaming/deleting/copying/Creating new folders and so on (For Navigation and general purpose work)
    val browsingPathDirectories: List<FileItem> = emptyList(), // Items(Dir) inside browsing path
    val errorMessage: String? = null, // Universal errorMessage if any operation fails
    val successMessage: String? = null, // Universal confirming success message for all successful operations
    val isLoading: Boolean = false, // Loading state of the page
)
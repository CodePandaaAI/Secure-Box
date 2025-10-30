package com.romit.securebox.data.model

data class FileBrowserUiState(
    val dirFiles: List<FileItem> = emptyList(),
    val error: String? = null,
    val selectedFile: FileItem? = null,
    val isLoading: Boolean = false
)
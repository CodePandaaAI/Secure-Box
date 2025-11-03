package com.romit.securebox.data.model

data class FileBrowserUiState(
    val currPath: String = "",
    val dirFiles: List<FileItem> = emptyList(),
    val error: String? = null,
    val isRenameEnabled: Boolean = false,
    val selectedFile: FileItem? = null,
    val newFileName: String = "",
    val successMessage: String? = null,
    val showDeleteDialog: Boolean = false,
    val isLoading: Boolean = false
)
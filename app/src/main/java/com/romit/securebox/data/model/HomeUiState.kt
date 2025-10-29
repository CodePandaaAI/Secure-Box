package com.romit.securebox.data.model

data class HomeUiState(
    val storageCategoriesList: List<StorageCategory> = emptyList(),
    val recentFiles: List<FileItem> = emptyList(),
    val selectedFile: FileItem? = null,
    val showBottomSheet: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false
)

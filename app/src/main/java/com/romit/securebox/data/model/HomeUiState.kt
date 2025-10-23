package com.romit.securebox.data.model

data class HomeUiState(
    val storageCategoriesList: List<StorageCategory> = emptyList(),
    val recentFiles: List<FileItem> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false
)

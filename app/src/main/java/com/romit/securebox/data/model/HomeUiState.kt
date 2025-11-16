package com.romit.securebox.data.model

data class HomeUiState(
    val recentFilesList: List<FileItem> = emptyList(),
    val storageCategories: List<StorageCategory> = emptyList(),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)
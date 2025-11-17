package com.romit.securebox.data.model

data class AllRecentsUiState(
    val allRecents: List<FileItem> = emptyList(),
    val isLoadingNextPage: Boolean = false,
    val successMessage: String? = null,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

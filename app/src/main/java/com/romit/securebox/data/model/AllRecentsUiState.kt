package com.romit.securebox.data.model

data class AllRecentsUiState(
    val files: List<FileItem> = emptyList(),
    val isLoadingNextPage: Boolean = false,
    val successMessage: String? = null,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

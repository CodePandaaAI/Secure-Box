package com.romit.securebox.presentation.featureHome

import com.romit.securebox.data.model.FileItem
import com.romit.securebox.data.model.StorageCategory

data class HomeUiState(
    val recentFilesList: List<FileItem> = emptyList(),
    val storageCategories: List<StorageCategory> = emptyList(),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)
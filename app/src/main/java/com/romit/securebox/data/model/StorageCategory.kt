package com.romit.securebox.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class StorageCategory(
    val name: String,
    val dirSize: String? = null,
    val path: String,
    val icon: ImageVector,
    val type: StorageCategoryType
)

enum class StorageCategoryType {
    DOWNLOADS,
    IMAGES,
    VIDEOS,
    MUSIC,
    DOCUMENTS,
    INTERNAL_STORAGE
}

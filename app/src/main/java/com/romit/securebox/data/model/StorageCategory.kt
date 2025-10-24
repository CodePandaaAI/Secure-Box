package com.romit.securebox.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class StorageCategory(
    val name: String,
    val dirSize: String? = null,
    val path: String,
    val icon: ImageVector
)

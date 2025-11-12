package com.romit.securebox.data.model

import android.os.Environment

data class DestinationPickerUiState(
    val currPath: String = Environment.getExternalStorageDirectory().absolutePath,
    val sourcePath: String = "",
    val directories: List<FileItem> = emptyList(),
    val isLoading: Boolean = false,
    val success: String? = null,
    val error: String? = null
)

package com.romit.securebox.screens.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screen {
    data object Home : Screen

    data class FileBrowser(val path: String) : Screen

    data object AllRecents : Screen

    data class DestinationScreen(val folderPath: String) : Screen

    data class FileDetails(val filePath: String) : Screen
}
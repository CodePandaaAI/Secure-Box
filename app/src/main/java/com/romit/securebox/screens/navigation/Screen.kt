package com.romit.securebox.screens.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen: NavKey {
    @Serializable
    data object Home : Screen

    @Serializable
    data class FileBrowser(val path: String) : Screen

    @Serializable
    data object AllRecents : Screen

    @Serializable
    data class DestinationScreen(val folderPath: String) : Screen

    @Serializable
    data class FileDetails(val filePath: String) : Screen
}
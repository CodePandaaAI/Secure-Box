package com.romit.securebox.screens.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    data object Home : Screen

    data class FileBrowser(val path: String) : Screen

    data object AllRecents: Screen

    data class DestinationScreen(val folderPath: String): Screen
}
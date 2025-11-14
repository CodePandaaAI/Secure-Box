package com.romit.securebox.screens.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data class FileBrowser(val path: String) : Screen

    @Serializable
    data object AllRecents: Screen
    @Serializable
    data class DestinationScreen(val folderPath: String): Screen
}
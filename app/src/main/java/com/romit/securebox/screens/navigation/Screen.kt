package com.romit.securebox.screens.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data class FileBrowser(val path: String) : Screen
}
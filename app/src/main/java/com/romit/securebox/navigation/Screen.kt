package com.romit.securebox.navigation

sealed interface Screen {
    data object Home : Screen

    data class FileBrowser(val path: String) : Screen

    data object AllRecents : Screen

    data class DestinationScreen(val folderPath: String) : Screen

//    data class FileDetails(val filePath: String) : Screen
}
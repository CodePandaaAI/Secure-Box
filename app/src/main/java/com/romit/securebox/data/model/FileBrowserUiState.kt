package com.romit.securebox.data.model

import android.os.Environment

data class FileBrowserUiState(
    val browsingPath: String = "", // Current Path for browsing folders accessing files/renaming/deleting/copying/Creating new folders and so on (For Navigation and general purpose work)
    val browsingPathDirectories: List<FileItem> = emptyList(), // Items(Dir) inside browsing path
    val operationTargetPath: String = Environment.getExternalStorageDirectory().absolutePath, // Path For Copy or Move Operations just for copying, moving files and creating folders
    val operationTargetPathDirectories: List<FileItem> = emptyList(), // Items(Dir) inside operation path
    val selectedFile: FileItem? = null, // Universal Selected File on which all of the operations are based on
    val showRenameInput: Boolean = false, // For renaming the selected file name
    val showDeleteDialog: Boolean = false, // For deleting the selected file
    val isCopyFile: Boolean = false, // Check for whether user want to copy or move file and based on that the ui behaves accordingly
    val isMoveFile: Boolean = false, // Check for whether user want to copy or move file and based on that the ui behaves accordingly
    val showCreateFolderDialog: Boolean = false, // For Creating new folder
    val newFileName: String = "", // For storing the file name while renaming
    val newFolderName: String = "", // For storing new folder name while naming
    val newFolderError: String? = null, // Any Error that occurs after trying to create folder pressing on create folder button, th error is shown directly in alert dialog
    val errorMessage: String? = null, // Universal error if any operation fails
    val successMessage: String? = null, // Universal confirming success message for all successful operations
    val isLoading: Boolean = false, // Loading state of the page

    // ✅ New fields from HomeScreen
    val storageCategoriesList: List<StorageCategory> = emptyList(),
    val recentFiles: List<FileItem> = emptyList(),
    val isRefreshing: Boolean = false
)
package com.romit.securebox.data.model

data class SharedFileOperationsUiState(
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
)
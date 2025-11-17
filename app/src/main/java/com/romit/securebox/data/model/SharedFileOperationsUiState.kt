package com.romit.securebox.data.model

import android.os.Environment
import com.romit.securebox.util.FileOperations

data class SharedFileOperationsUiState(
    val selectedFile: FileItem? = null, // Universal Selected File on which all of the operations are based on
    val operationSourceFile: FileItem? = null, // NEW: File being copied/moved
    val showRenameInput: Boolean = false, // For renaming the selected file name
    val showDeleteDialog: Boolean = false, // For deleting the selected file
    val selectedOperation: FileOperations = FileOperations.NONE,
    val showCreateFolderDialog: Boolean = false, // For Creating new folder
    val newFileName: String = "", // For storing the file name while renaming
    val newFolderName: String = "", // For storing new folder name while naming
    val newFolderError: String? = null, // Any Error that occurs after trying to create folder pressing on create folder button, th errorMessage is shown directly in alert dialog
    val errorMessage: String? = null, // Universal errorMessage if any operation fails
    val successMessage: String? = null, // Universal confirming success message for all successful operations

    // Destination Screen State
    val operationTargetPath: String = Environment.getExternalStorageDirectory().absolutePath, // Path For Copy or Move Operations just for copying, moving allRecents and creating folders
    val operationTargetPathDirectories: List<FileItem> = emptyList(), // Items(Dir) inside operation path
    val isDestinationScreenLoading: Boolean = false,
)
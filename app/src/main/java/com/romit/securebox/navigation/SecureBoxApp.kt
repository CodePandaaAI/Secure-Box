package com.romit.securebox.navigation

import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.romit.securebox.components.BottomFileInfoSheet
import com.romit.securebox.components.CreateFolderDialog
import com.romit.securebox.components.DeleteDialog
import com.romit.securebox.components.FileDetailsPane
import com.romit.securebox.components.FileOperationBottomAppBar
import com.romit.securebox.components.RenameDialog
import com.romit.securebox.presentation.featureBrowse.FileBrowserScreen
import com.romit.securebox.presentation.featureDestination.DestinationScreen
import com.romit.securebox.presentation.featureHome.HomeScreen
import com.romit.securebox.presentation.featureRecents.AllRecentsScreen
import com.romit.securebox.presentation.sharedViewmodel.NavigationViewModel
import com.romit.securebox.presentation.sharedViewmodel.SharedFileOperationsUiEvent
import com.romit.securebox.presentation.sharedViewmodel.SharedFileOperationsViewModel
import com.romit.securebox.util.FileOperations
import com.romit.securebox.util.openFile
import kotlinx.coroutines.launch

/**
 * The main entry point composable for the SecureBox application.
 * A composable that serves as the main navigation and layout structure for the application.
 * It manages the display of different screens based on the navigation back stack, handles
 * back-press events, and adapts its layout for different window sizes (e.g., phones vs. tablets).
 *
 * This function integrates a `Scaffold` to provide a consistent layout with a top app bar,
 * a bottom app bar for file operations, and a snackbar host. It uses a `NavDisplay` to render
 * the current screen from the back stack, employing a `ListDetailSceneStrategy` for adaptive layouts
 * on larger screens.
 *
 * It also orchestrates the display of various dialogs and bottom sheets for file operations like
 * renaming, deleting, copying, and moving files, by observing the state from the provided ViewModels.
 *
 * sharedFileOperationsViewModel The ViewModel that manages the state and logic for file operations
 * (copy, move, delete, rename) and UI state related to these operations (e.g., dialog visibility).
 *
 */
@Composable
fun SecureBoxApp(
) {
    val navigationViewModel = hiltViewModel<NavigationViewModel>()
    val sharedFileOperationsViewModel =
        hiltViewModel<SharedFileOperationsViewModel>(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
    val snackBarHostState = remember { SnackbarHostState() }
    val uiState by sharedFileOperationsViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val currentScreen = navigationViewModel.currentScreen

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen != null && currentScreen !is Screen.Home) {
                AppTopBar(
                    onBackClick = {
                        navigationViewModel.removeLastOrNull()
                    }
                )
            }
        },
        bottomBar = {
            if (currentScreen is Screen.DestinationScreen && uiState.selectedFile != null) {
                FileOperationBottomAppBar(
                    onCreateFolder = { sharedFileOperationsViewModel.toggleCreateFolderDialog() },
                    onConfirmLocation = {
                        if (uiState.selectedFile!!.path.isNotBlank()) {
                            if (uiState.selectedOperation == FileOperations.COPY) {
                                sharedFileOperationsViewModel.copyFile(
                                    uiState.selectedFile!!.path,
                                    uiState.operationTargetPath
                                )
                            }
                            if (uiState.selectedOperation == FileOperations.MOVE) {
                                sharedFileOperationsViewModel.moveFile(
                                    uiState.selectedFile!!.path,
                                    uiState.operationTargetPath
                                )
                            }
                            navigationViewModel.removeLastOrNull()
                        } else {
                            scope.launch {
                                snackBarHostState.showSnackbar(
                                    message = "No file selected to paste.",
                                    withDismissAction = true
                                )
                            }
                        }
                    },
                    buttonLabel = when (uiState.selectedOperation) {
                        FileOperations.COPY -> "Copy Here"
                        FileOperations.MOVE -> "Move Here"
                        else -> ""
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) { innerPadding ->

        LaunchedEffect(Unit) {
            sharedFileOperationsViewModel.uiEvents.collect { event ->
                when (event) {
                    is SharedFileOperationsUiEvent.ShowSnackBar -> snackBarHostState.showSnackbar(event.message)
                }
            }
        }

        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = navigationViewModel.backStack,
            onBack = { navigationViewModel.removeLastOrNull() },
            // Removed all animations
            transitionSpec = {
                EnterTransition.None togetherWith ExitTransition.None
            },
            popTransitionSpec = {
                EnterTransition.None togetherWith ExitTransition.None
            },
            predictivePopTransitionSpec = {
                EnterTransition.None togetherWith ExitTransition.None
            },
            entryProvider = { route ->
                when (route) {
                    is Screen.Home -> NavEntry(route) {
                        HomeScreen(
                            onCategoryClicked = { path ->
                                navigationViewModel.navigateTo(Screen.FileBrowser(path))
                            },
                            onOpenFile = { file ->
                                if (file.isDirectory) {
                                    navigationViewModel.navigateTo(Screen.FileBrowser(path = file.path))
                                } else {
                                    openFile(context, file)
                                }
                            },
                            onShowAllRecents = {
                                navigationViewModel.navigateTo(Screen.AllRecents)
                            }
                        )
                    }

                    is Screen.AllRecents -> NavEntry(route) {
                        AllRecentsScreen(
                            onFileClicked = { file ->
                                if (file.isDirectory) {
                                    navigationViewModel.navigateTo(Screen.FileBrowser(path = file.path))
                                } else {
                                    openFile(context, file)
                                }
                            },
                        )
                    }

                    is Screen.FileBrowser -> NavEntry(route) {
                        FileBrowserScreen(
                            path = route.path,
                            onFileClicked = { file ->
                                if (file.isDirectory) {
                                   navigationViewModel.navigateTo(Screen.FileBrowser(path = file.path))
                                } else {
                                    openFile(context, file)
                                }
                            }
                        )
                    }

                    is Screen.DestinationScreen -> NavEntry(route) {
                        LaunchedEffect(Unit) {
                            sharedFileOperationsViewModel.initializeDestinationScreen(route.folderPath)
                        }

                        DestinationScreen(
                            onFolderClicked = { folder ->
                                sharedFileOperationsViewModel.navigateToFolder(folder.path)
                            },
                            onNavigateBack = {
                                navigationViewModel.removeLastOrNull()
                            }
                        )
                    }

                    is Screen.FileDetails -> NavEntry(route) {
                        FileDetailsPane(
                            selectedFile = uiState.selectedFile,
                            onClose = {
                                // Close detail pane and clear selection
                                sharedFileOperationsViewModel.selectedFileForBottomSheet(null)
                               navigationViewModel.removeLastOrNull()
                            },
                            onCopyTo = {
                                sharedFileOperationsViewModel.clearAllOperationsState()
                                sharedFileOperationsViewModel.chooseOperation(FileOperations.COPY)
                                sharedFileOperationsViewModel.selectedFileForBottomSheet(null)
                                navigationViewModel.removeIf { it is Screen.FileDetails }
                               navigationViewModel.navigateTo(
                                    Screen.DestinationScreen(
                                        folderPath = Environment.getExternalStorageDirectory().absolutePath
                                    )
                                )
                            },
                            onMoveTo = {
                                sharedFileOperationsViewModel.clearAllOperationsState()
                                sharedFileOperationsViewModel.chooseOperation(FileOperations.MOVE)
                                sharedFileOperationsViewModel.selectedFileForBottomSheet(null)
                                navigationViewModel.removeIf { it is Screen.FileDetails }
                                navigationViewModel.navigateTo(
                                    Screen.DestinationScreen(
                                        folderPath = Environment.getExternalStorageDirectory().absolutePath
                                    )
                                )
                            },
                        )
                    }
                }
            }
        )
        if (uiState.selectedFile != null) {
            BottomFileInfoSheet(
                onDismiss = { sharedFileOperationsViewModel.selectedFileForBottomSheet(null) },
                onOpenDeleteDialog = { sharedFileOperationsViewModel.toggleDeleteDialog() },
                onOpenRenameDialog = { sharedFileOperationsViewModel.toggleRenameDialog() },
                selectedFile = { uiState.selectedFile!! },
                onCopyTo = {
                    sharedFileOperationsViewModel.clearAllOperationsState()
                    sharedFileOperationsViewModel.chooseOperation(FileOperations.COPY)
                    sharedFileOperationsViewModel.selectedFileForBottomSheet(null)
                   navigationViewModel.navigateTo(
                        Screen.DestinationScreen(
                            folderPath = Environment.getExternalStorageDirectory().absolutePath
                        )
                    )
                },
                onMoveTo = {
                    sharedFileOperationsViewModel.clearAllOperationsState()
                    sharedFileOperationsViewModel.chooseOperation(FileOperations.MOVE)
                    sharedFileOperationsViewModel.selectedFileForBottomSheet(null)
                    navigationViewModel.navigateTo(
                        Screen.DestinationScreen(
                            folderPath = Environment.getExternalStorageDirectory().absolutePath
                        )
                    )
                }
            )
        }
        // Rename Dialog
        if (uiState.showRenameDialog && uiState.selectedFile != null) {
            RenameDialog(
                onDismissRequest = { sharedFileOperationsViewModel.toggleRenameDialog() },
                onCancel = { sharedFileOperationsViewModel.toggleRenameDialog() },
                onRenamingFile = { sharedFileOperationsViewModel.onRenamingFile(it) },
                onRenameFileClicked = { sharedFileOperationsViewModel.onRenameFileClicked() },
                newFileName = { uiState.newFileName },
                selectedFile = { uiState.selectedFile!! }
            )
        }

        // Delete Dialog
        if (uiState.showDeleteDialog && uiState.selectedFile != null) {
            DeleteDialog(
                onDismissRequest = { sharedFileOperationsViewModel.toggleDeleteDialog() },
                onConfirmDelete = {
                    sharedFileOperationsViewModel.deleteFile(uiState.selectedFile!!.path)
                    sharedFileOperationsViewModel.toggleDeleteDialog()
                    sharedFileOperationsViewModel.selectedFileForBottomSheet(null)
                },
                selectedFile = { uiState.selectedFile!! }
            )
        }

        if (uiState.showCreateFolderDialog) {
            CreateFolderDialog(
                folderName = uiState.newFolderName,
                error = uiState.newFolderError,
                onFolderNameChange = {
                    sharedFileOperationsViewModel.updateNewFolderName(
                        it
                    )
                },
                onConfirmFolderCreation = { sharedFileOperationsViewModel.createFolder() },
                onDismissFolderDialog = { sharedFileOperationsViewModel.toggleCreateFolderDialog() }
            )
        }
    }
}

/**
 * A composable function that displays the top app bar for the application.
 * It includes a title and a back navigation button.
 *
 * @param onBackClick A lambda function to be executed when the back navigation icon is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { Text("Secure Box") },
        navigationIcon = {
            IconButton(
                modifier = Modifier.padding(horizontal = 8.dp),
                onClick = onBackClick, colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }

        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    )
}
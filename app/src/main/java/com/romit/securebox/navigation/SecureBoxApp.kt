package com.romit.securebox.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.romit.securebox.components.app.AppTopBar
import com.romit.securebox.components.dialogs.DialogsAndBottomSheet
import com.romit.securebox.components.app.FileOperationBottomAppBar
import com.romit.securebox.presentation.featureBrowse.FileBrowserScreen
import com.romit.securebox.presentation.featureDestination.DestinationScreen
import com.romit.securebox.presentation.featureHome.HomeScreen
import com.romit.securebox.presentation.featureRecents.AllRecentsScreen
import com.romit.securebox.presentation.sharedViewmodel.NavigationViewModel
import com.romit.securebox.presentation.sharedViewmodel.SharedFileOperationsUiEvent
import com.romit.securebox.presentation.sharedViewmodel.SharedFileOperationsViewModel
import com.romit.securebox.util.openFile

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
    val activity = LocalActivity.current as ComponentActivity

    val navigationViewModel = hiltViewModel<NavigationViewModel>(viewModelStoreOwner = activity)

    val sharedFileOperationsViewModel =
        hiltViewModel<SharedFileOperationsViewModel>(viewModelStoreOwner = activity)

    val snackBarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar()
        },
        bottomBar = {
            FileOperationBottomAppBar(
                onCreateFolder = { sharedFileOperationsViewModel.toggleCreateFolderDialog() },
                onConfirmLocation = {
                    navigationViewModel.removeAllDestinationScreens()
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) { innerPadding ->

        LaunchedEffect(Unit) {
            sharedFileOperationsViewModel.uiEvents.collect { event ->
                when (event) {
                    is SharedFileOperationsUiEvent.ShowSnackBar -> snackBarHostState.showSnackbar(
                        event.message
                    )
                }
            }
        }
        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = navigationViewModel.backStack,
            onBack = {
                navigationViewModel.removeLastOrNull()
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
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

                    is Screen.DestinationScreen -> NavEntry(route, contentKey = route.folderPath) {
                        DestinationScreen(
                            currentRoute = route,
                            onFolderClicked = { folder ->
                                sharedFileOperationsViewModel.updateOperationPathAndFetchDirectories(
                                    folder.path
                                )
                                navigationViewModel.navigateToFolder(
                                    route,
                                    Screen.DestinationScreen(folder.path)
                                )
                            }
                        )
                    }

//                    is Screen.FileDetails -> NavEntry(route) {
//                        FileDetailsPane(
//                            selectedFile = uiState.selectedFile,
//                            onClose = {
//                                // Close detail pane and clear selection
//                                sharedFileOperationsViewModel.selectedFileForBottomSheet(null)
//                                navigationViewModel.removeLastOrNull()
//                            },
//                            onCopyTo = {
//                                sharedFileOperationsViewModel.clearAllOperationsState()
//                                sharedFileOperationsViewModel.chooseOperation(FileOperations.COPY)
//                                navigationViewModel.removeIf { it is Screen.FileDetails }
//                                navigationViewModel.navigateTo(
//                                    Screen.DestinationScreen(
//                                        folderPath = Environment.getExternalStorageDirectory().absolutePath
//                                    )
//                                )
//                                sharedFileOperationsViewModel.updateOperationPathAndFetchDirectories(
//                                    Environment.getExternalStorageDirectory().absolutePath
//                                )
//                            },
//                            onMoveTo = {
//                                sharedFileOperationsViewModel.clearAllOperationsState()
//                                sharedFileOperationsViewModel.chooseOperation(FileOperations.MOVE)
//                                navigationViewModel.removeIf { it is Screen.FileDetails }
//                                navigationViewModel.navigateTo(
//                                    Screen.DestinationScreen(
//                                        folderPath = Environment.getExternalStorageDirectory().absolutePath
//                                    )
//                                )
//                                sharedFileOperationsViewModel.updateOperationPathAndFetchDirectories(
//                                    Environment.getExternalStorageDirectory().absolutePath
//                                )
//                                Log.d(
//                                    "Current Back Stack",
//                                    navigationViewModel.backStack.toString()
//                                )
//                            },
//                        )
//                    }
                }
            }
        )
        DialogsAndBottomSheet()
    }
}
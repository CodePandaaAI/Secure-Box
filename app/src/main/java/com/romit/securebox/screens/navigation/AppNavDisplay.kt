package com.romit.securebox.screens.navigation

import android.os.Environment
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.romit.securebox.components.FileOperationBottomAppBar
import com.romit.securebox.screens.AllRecentsScreen
import com.romit.securebox.screens.DestinationScreen
import com.romit.securebox.screens.FileBrowserScreen
import com.romit.securebox.screens.HomeScreen
import com.romit.securebox.util.FileOperations
import com.romit.securebox.util.openFile
import com.romit.securebox.viewmodels.FileBrowserScreenViewModel
import com.romit.securebox.viewmodels.SharedFileOperationsViewModel
import kotlinx.coroutines.launch


@Composable
fun SecureApp() {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }
    AppNavDisplay(backStack)
}

@Composable
fun AppNavDisplay(backStack: SnapshotStateList<Screen>) {
    val sharedFileBrowserViewModel: FileBrowserScreenViewModel = hiltViewModel()
    val sharedFileOperationsViewModel: SharedFileOperationsViewModel = hiltViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by sharedFileOperationsViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val currentScreen = backStack.lastOrNull()

    val isHomeScreen = currentScreen is Screen.Home
    val isDestinationScreen = currentScreen is Screen.DestinationScreen


    val canGoBack = backStack.size > 1

    BackHandler(enabled = canGoBack) {
        backStack.removeLastOrNull()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen != null && !isHomeScreen) {
                AppTopBar(
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        },
        bottomBar = {
            if (isDestinationScreen && uiState.operationSourceFile != null) {
                FileOperationBottomAppBar(
                    onCreateFolder = { sharedFileOperationsViewModel.toggleCreateFolderDialog() },
                    onConfirmLocation = {
                        if (uiState.operationSourceFile!!.path.isNotBlank()) {
                            scope.launch {
                                if (uiState.selectedOperation == FileOperations.COPY) {
                                    sharedFileOperationsViewModel.copyFile(
                                        uiState.operationSourceFile!!.path,
                                        uiState.operationTargetPath
                                    )
                                }
                                if (uiState.selectedOperation == FileOperations.MOVE) {
                                    sharedFileOperationsViewModel.moveFile(
                                        uiState.operationSourceFile!!.path,
                                        uiState.operationTargetPath
                                    )
                                }
                                backStack.removeLastOrNull()
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        NavDisplay(modifier = Modifier.padding(innerPadding),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },

            entryProvider = { route ->
                when (route) {
                    is Screen.Home -> NavEntry(route) {
                        HomeScreen(
                            sharedFileOperationsUiState = uiState,
                            snackbarHostState = snackbarHostState,
                            onCategoryClicked = { path ->
                                backStack.add(Screen.FileBrowser(path))
                            },
                            sharedFileOperationsViewModel = sharedFileOperationsViewModel,
                            onFileClicked = { file ->
                                if (file.isDirectory) {
                                    backStack.add(Screen.FileBrowser(path = file.path))
                                } else {
                                    openFile(context, file)
                                }
                            },
                            onShowAllRecents = {
                                backStack.add(Screen.AllRecents)
                            },
                            onCopyTo = { file -> // ✅ Receives the file from bottom sheet
                                sharedFileOperationsViewModel.clearAllOperationsState()
                                sharedFileOperationsViewModel.setOperationSourceFile(file) // ✅ Store for operation
                                sharedFileOperationsViewModel.chooseOperation(FileOperations.COPY)
                                sharedFileOperationsViewModel.selectedFileForBottomSheet(null) // ✅ Close bottom sheet
                                backStack.add(Screen.DestinationScreen(
                                        folderPath = Environment.getExternalStorageDirectory().absolutePath
                                    )
                                )
                            },
                            onMoveTo = { file -> // ✅ Receives the file from bottom sheet
                                sharedFileOperationsViewModel.clearAllOperationsState()
                                sharedFileOperationsViewModel.setOperationSourceFile(file) // ✅ Store for operation
                                sharedFileOperationsViewModel.chooseOperation(FileOperations.MOVE)
                                sharedFileOperationsViewModel.selectedFileForBottomSheet(null) // ✅ Close bottom sheet
                                backStack.add(
                                    Screen.DestinationScreen(
                                        folderPath = Environment.getExternalStorageDirectory().absolutePath
                                    )
                                )
                            }
                        )
                    }

                    is Screen.AllRecents -> NavEntry(route) {
                        AllRecentsScreen(
                            sharedFileOperationsUiState = uiState,
                            snackbarHostState = snackbarHostState, onFileClicked = { file ->
                                if (file.isDirectory) {
                                    backStack.add(Screen.FileBrowser(path = file.path))
                                } else {
                                    openFile(context, file)
                                }
                            },
                            sharedFileOperationsViewModel = sharedFileOperationsViewModel,
                            onCopyTo = { file -> // ✅ Receives the file from bottom sheet
                                sharedFileOperationsViewModel.clearAllOperationsState()
                                sharedFileOperationsViewModel.setOperationSourceFile(file) // ✅ Store for operation
                                sharedFileOperationsViewModel.chooseOperation(FileOperations.COPY)
                                sharedFileOperationsViewModel.selectedFileForBottomSheet(null) // ✅ Close bottom sheet
                                backStack.add(
                                    Screen.DestinationScreen(
                                        folderPath = Environment.getExternalStorageDirectory().absolutePath
                                    )
                                )
                            },
                            onMoveTo = { file -> // ✅ Receives the file from bottom sheet
                                sharedFileOperationsViewModel.clearAllOperationsState()
                                sharedFileOperationsViewModel.setOperationSourceFile(file) // ✅ Store for operation
                                sharedFileOperationsViewModel.chooseOperation(FileOperations.MOVE)
                                sharedFileOperationsViewModel.selectedFileForBottomSheet(null) // ✅ Close bottom sheet
                                backStack.add(
                                    Screen.DestinationScreen(
                                        folderPath = Environment.getExternalStorageDirectory().absolutePath
                                    )
                                )
                            }
                        )
                    }

                    is Screen.FileBrowser -> NavEntry(route) {
                        FileBrowserScreen(
                            sharedFileOperationsUiState = uiState,
                            snackbarHostState = snackbarHostState,
                            sharedFileOperationsViewModel = sharedFileOperationsViewModel,
                            fileBrowserScreenViewModel = sharedFileBrowserViewModel,
                            path = route.path,
                            onFileClicked = { file ->
                                if (file.isDirectory) {
                                    backStack.add(Screen.FileBrowser(path = file.path))
                                } else {
                                    openFile(context, file)
                                }
                            },
                            onCopyTo = { file -> // ✅ Receives the file from bottom sheet
                                sharedFileOperationsViewModel.clearAllOperationsState()
                                sharedFileOperationsViewModel.setOperationSourceFile(file) // ✅ Store for operation
                                sharedFileOperationsViewModel.chooseOperation(FileOperations.COPY)
                                sharedFileOperationsViewModel.selectedFileForBottomSheet(null) // ✅ Close bottom sheet
                                backStack.add(
                                    Screen.DestinationScreen(
                                        folderPath = Environment.getExternalStorageDirectory().absolutePath
                                    )
                                )
                            },
                            onMoveTo = { file -> // ✅ Receives the file from bottom sheet
                                sharedFileOperationsViewModel.clearAllOperationsState()
                                sharedFileOperationsViewModel.setOperationSourceFile(file) // ✅ Store for operation
                                sharedFileOperationsViewModel.chooseOperation(FileOperations.MOVE)
                                sharedFileOperationsViewModel.selectedFileForBottomSheet(null) // ✅ Close bottom sheet
                                backStack.add(
                                    Screen.DestinationScreen(
                                        folderPath = Environment.getExternalStorageDirectory().absolutePath
                                    )
                                )
                            }
                        )
                    }

                    is Screen.DestinationScreen -> NavEntry(route) {
                        // ✅ Initialize DestinationScreen with starting path
                        LaunchedEffect(Unit) {
                            sharedFileOperationsViewModel.initializeDestinationScreen(route.folderPath)
                        }

                        DestinationScreen(
                            sharedFileOperationsViewModel = sharedFileOperationsViewModel,
                            onFolderClicked = { folder ->
                                // ✅ Use navigateToFolder instead of direct updateCurrentPath
                                sharedFileOperationsViewModel.navigateToFolder(folder.path)
                            },
                            onNavigateBack = {
                                // ✅ Exit DestinationScreen completely
                                backStack.removeLastOrNull()
                            }
                        )
                    }
                }
            }
        )
    }
}

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
                    containerColor = if (isSystemInDarkTheme()) Color.Gray.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
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
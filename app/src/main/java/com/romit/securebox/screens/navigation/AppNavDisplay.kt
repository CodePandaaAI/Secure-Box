package com.romit.securebox.screens.navigation

import android.os.Environment
import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
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
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass
import com.romit.securebox.components.BottomFileInfoSheet
import com.romit.securebox.components.DeleteDialog
import com.romit.securebox.components.FileDetailsPane
import com.romit.securebox.components.FileOperationBottomAppBar
import com.romit.securebox.components.RenameDialog
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

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppNavDisplay(backStack: SnapshotStateList<Screen>) {
    val sharedFileBrowserViewModel: FileBrowserScreenViewModel = hiltViewModel()
    val sharedFileOperationsViewModel: SharedFileOperationsViewModel = hiltViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by sharedFileOperationsViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val windowSizeClass = windowAdaptiveInfo.windowSizeClass

//    val isCompact = !windowSizeClass.isWidthAtLeastBreakpoint(
//        WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
//    )
    val isTablet = windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
    )

    val currentScreen = backStack.lastOrNull()

    val isHomeScreen = currentScreen is Screen.Home
    val isDestinationScreen = currentScreen is Screen.DestinationScreen

    val listDetailStrategy = rememberListDetailSceneStrategy<Screen>()


    val canGoBack = backStack.size > 1

    BackHandler(enabled = canGoBack) {
        backStack.removeLastOrNull()
    }

    // ✅ Handle file selection based on device type
    LaunchedEffect(uiState.selectedFile, isTablet) {
        if (uiState.selectedFile != null && isTablet) {
            // On tablets: Navigate to FileDetails pane
            val existingDetails = backStack.find { it is Screen.FileDetails }
            if (existingDetails == null) {
                backStack.add(Screen.FileDetails(uiState.selectedFile!!.path))
            }
        }
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

        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            sceneStrategy = listDetailStrategy,
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
                    is Screen.Home -> NavEntry(
                        route, metadata = ListDetailSceneStrategy.listPane(
                            detailPlaceholder = {
                                // Show when no folder is selected
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.Folder,
                                            contentDescription = null,
                                            modifier = Modifier.size(64.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("Select a category to browse files")
                                    }
                                }
                            }
                        )) {
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
                            }
                        )
                    }

                    is Screen.AllRecents -> NavEntry(route, ListDetailSceneStrategy.detailPane()) {
                        AllRecentsScreen(
                            sharedFileOperationsUiState = uiState,
                            snackbarHostState = snackbarHostState, onFileClicked = { file ->
                                if (file.isDirectory) {
                                    backStack.add(Screen.FileBrowser(path = file.path))
                                } else {
                                    openFile(context, file)
                                }
                            },
                            sharedFileOperationsViewModel = sharedFileOperationsViewModel
                        )
                    }

                    is Screen.FileBrowser -> NavEntry(
                        route,
                        metadata = ListDetailSceneStrategy.detailPane()
                    ) {
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

                    is Screen.FileDetails -> NavEntry(
                        route,
                        metadata = ListDetailSceneStrategy.extraPane()
                    ) {
                        FileDetailsPane(
                            selectedFile = uiState.selectedFile,
                            sharedFileOperationsViewModel = sharedFileOperationsViewModel,
                            onClose = {
                                // Close detail pane and clear selection
                                sharedFileOperationsViewModel.selectedFileForBottomSheet(null)
                                backStack.removeLastOrNull()
                            },
                            onCopyTo = { file ->
                                sharedFileOperationsViewModel.clearAllOperationsState()
                                sharedFileOperationsViewModel.setOperationSourceFile(file)
                                sharedFileOperationsViewModel.chooseOperation(FileOperations.COPY)
                                sharedFileOperationsViewModel.selectedFileForBottomSheet(null)
                                backStack.removeIf { it is Screen.FileDetails }
                                backStack.add(
                                    Screen.DestinationScreen(
                                        folderPath = Environment.getExternalStorageDirectory().absolutePath
                                    )
                                )
                            },
                            onMoveTo = { file ->
                                sharedFileOperationsViewModel.clearAllOperationsState()
                                sharedFileOperationsViewModel.setOperationSourceFile(file)
                                sharedFileOperationsViewModel.chooseOperation(FileOperations.MOVE)
                                sharedFileOperationsViewModel.selectedFileForBottomSheet(null)
                                backStack.removeIf { it is Screen.FileDetails }
                                backStack.add(
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
        if (!isTablet && uiState.selectedFile != null) {
            BottomFileInfoSheet(
                onDismiss = { sharedFileOperationsViewModel.selectedFileForBottomSheet(null) },
                onOpenDeleteDialog = { sharedFileOperationsViewModel.toggleDeleteDialog() },
                onOpenRenameDialog = { sharedFileOperationsViewModel.toggleRenameDialog() },
                selectedFile = { uiState.selectedFile!! },
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
        // Rename Dialog
        if (uiState.showRenameInput && uiState.selectedFile != null) {
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
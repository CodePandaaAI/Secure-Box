package com.romit.securebox.screens.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
    val navController = rememberNavController()
    AppNavHost(navController)
}

@Composable
fun AppNavHost(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isHomeScreen = currentBackStackEntry?.destination?.hasRoute<Screen.Home>() == true
    val isDestinationScreen =
        currentBackStackEntry?.destination?.hasRoute<Screen.DestinationScreen>() == true
    val sharedFileBrowserViewModel: FileBrowserScreenViewModel = hiltViewModel()
    val sharedFileOperationsViewModel: SharedFileOperationsViewModel = hiltViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by sharedFileOperationsViewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!isHomeScreen) {
                AppTopBar(
                    onBackClick = {
                        navController.popBackStack()
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
                                navController.popBackStack()
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
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = Screen.Home,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable<Screen.Home> {
                HomeScreen(
                    sharedFileOperationsUiState = uiState,
                    snackbarHostState = snackbarHostState,
                    onCategoryClicked = { path ->
                        navController.navigate(Screen.FileBrowser(path)) {
                            launchSingleTop = true
                        }
                    },
                    sharedFileOperationsViewModel = sharedFileOperationsViewModel,
                    onFileClicked = { file ->
                        if (file.isDirectory) {
                            navController.navigate(Screen.FileBrowser(path = file.path))
                        } else {
                            openFile(context, file)
                        }
                    },
                    onShowAllRecents = {
                        navController.navigate(Screen.AllRecents)
                    },
                    onCopyTo = { file -> // ✅ Receives the file from bottom sheet
                        sharedFileOperationsViewModel.clearAllOperationsState()
                        sharedFileOperationsViewModel.setOperationSourceFile(file) // ✅ Store for operation
                        sharedFileOperationsViewModel.chooseOperation(FileOperations.COPY)
                        sharedFileOperationsViewModel.selectedFileForBottomSheet(null) // ✅ Close bottom sheet
                        navController.navigate(Screen.DestinationScreen)
                    },
                    onMoveTo = { file -> // ✅ Receives the file from bottom sheet
                        sharedFileOperationsViewModel.clearAllOperationsState()
                        sharedFileOperationsViewModel.setOperationSourceFile(file) // ✅ Store for operation
                        sharedFileOperationsViewModel.chooseOperation(FileOperations.MOVE)
                        sharedFileOperationsViewModel.selectedFileForBottomSheet(null) // ✅ Close bottom sheet
                        navController.navigate(Screen.DestinationScreen)
                    }
                )
            }

            composable<Screen.FileBrowser> { backStackEntry ->
                val path = backStackEntry.toRoute<Screen.FileBrowser>().path
                FileBrowserScreen(
                    sharedFileOperationsUiState = uiState,
                    snackbarHostState = snackbarHostState,
                    sharedFileOperationsViewModel = sharedFileOperationsViewModel,
                    fileBrowserScreenViewModel = sharedFileBrowserViewModel,
                    path = path,
                    onFileClicked = { file ->
                        if (file.isDirectory) {
                            navController.navigate(Screen.FileBrowser(path = file.path))
                        } else {
                            openFile(context, file)
                        }
                    },
                    onCopyTo = { file -> // ✅ Receives the file from bottom sheet
                        sharedFileOperationsViewModel.clearAllOperationsState()
                        sharedFileOperationsViewModel.setOperationSourceFile(file) // ✅ Store for operation
                        sharedFileOperationsViewModel.chooseOperation(FileOperations.COPY)
                        sharedFileOperationsViewModel.selectedFileForBottomSheet(null) // ✅ Close bottom sheet
                        navController.navigate(Screen.DestinationScreen)
                    },
                    onMoveTo = { file -> // ✅ Receives the file from bottom sheet
                        sharedFileOperationsViewModel.clearAllOperationsState()
                        sharedFileOperationsViewModel.setOperationSourceFile(file) // ✅ Store for operation
                        sharedFileOperationsViewModel.chooseOperation(FileOperations.MOVE)
                        sharedFileOperationsViewModel.selectedFileForBottomSheet(null) // ✅ Close bottom sheet
                        navController.navigate(Screen.DestinationScreen)
                    }
                )
            }
            composable<Screen.AllRecents> {
                AllRecentsScreen(
                    sharedFileOperationsUiState = uiState,
                    snackbarHostState = snackbarHostState, onFileClicked = { file ->
                        if (file.isDirectory) {
                            navController.navigate(Screen.FileBrowser(path = file.path))
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
                        navController.navigate(Screen.DestinationScreen)
                    },
                    onMoveTo = { file -> // ✅ Receives the file from bottom sheet
                        sharedFileOperationsViewModel.clearAllOperationsState()
                        sharedFileOperationsViewModel.setOperationSourceFile(file) // ✅ Store for operation
                        sharedFileOperationsViewModel.chooseOperation(FileOperations.MOVE)
                        sharedFileOperationsViewModel.selectedFileForBottomSheet(null) // ✅ Close bottom sheet
                        navController.navigate(Screen.DestinationScreen)
                    }
                )
            }


            composable<Screen.DestinationScreen> {
                DestinationScreen(
                    sharedFileOperationsUiState = uiState,
                    sharedFileOperationsViewModel,
                    onFolderClicked = {
                        sharedFileOperationsViewModel.updateCurrentPath(it.path)
                        navController.navigate(Screen.DestinationScreen)
                    }
                )
            }

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
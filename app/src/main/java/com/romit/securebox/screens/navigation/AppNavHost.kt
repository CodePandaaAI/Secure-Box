package com.romit.securebox.screens.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.romit.securebox.screens.AllRecentsScreen
import com.romit.securebox.screens.DestinationScreen
import com.romit.securebox.screens.FileBrowserScreen
import com.romit.securebox.screens.HomeScreen
import com.romit.securebox.util.openFile
import com.romit.securebox.viewmodels.DestinationPickerViewModel
import com.romit.securebox.viewmodels.FileBrowserScreenViewModel
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
    val isDestinationPickerScreen =
        currentBackStackEntry?.destination?.parent?.hasRoute<Screen.DestinationPicker>() == true
    val sharedFileBrowserViewModel: FileBrowserScreenViewModel = hiltViewModel()
    val destinationPickerViewModel: DestinationPickerViewModel = hiltViewModel()
    val snackbarHostState = remember { SnackbarHostState() }

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
            if (isDestinationPickerScreen) {
                BottomBar(
                    onCreateFolder = {},
                    onConfirmLocation = {
                        val selectedFile = destinationPickerViewModel.uiState.value.sourcePath
                        if (selectedFile.isNotBlank()) {
                            if (destinationPickerViewModel.uiState.value.isCopyFile) {
                                destinationPickerViewModel.copyFile(
                                    selectedFile,
                                    destinationPickerViewModel.uiState.value.currPath
                                )
                            } else {
                                destinationPickerViewModel.moveFile(
                                    selectedFile,
                                    destinationPickerViewModel.uiState.value.currPath
                                )
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
                    buttonLabel = if (destinationPickerViewModel.uiState.value.isCopyFile) "Copy Here" else "Move Here"
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
                    snackbarHostState = snackbarHostState,
                    onCategoryClicked = { path ->
                        navController.navigate(Screen.FileBrowser(path)) {
                            launchSingleTop = true
                        }
                    },
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
                    onCopyTo = {
                        destinationPickerViewModel.addSourcePath(it.path)
                        destinationPickerViewModel.isCopyFile()
                        navController.navigate(Screen.DestinationPicker)
                    },
                    onMoveTo = {
                        destinationPickerViewModel.addSourcePath(it.path)
                        destinationPickerViewModel.isMoveFile()
                        navController.navigate(Screen.DestinationPicker)
                    }
                )
            }

            composable<Screen.FileBrowser> { backStackEntry ->
                val path = backStackEntry.toRoute<Screen.FileBrowser>().path
                FileBrowserScreen(
                    snackbarHostState = snackbarHostState,
                    viewModel = sharedFileBrowserViewModel,
                    path = path,
                    onFileClicked = { file ->
                        if (file.isDirectory) {
                            navController.navigate(Screen.FileBrowser(path = file.path))
                        } else {
                            openFile(context, file)
                        }
                    },
                    onCopyTo = {
                        destinationPickerViewModel.addSourcePath(it.path)
                        destinationPickerViewModel.isCopyFile()
                        navController.navigate(Screen.DestinationPicker)
                    },
                    onMoveTo = {
                        destinationPickerViewModel.addSourcePath(it.path)
                        destinationPickerViewModel.isMoveFile()
                        navController.navigate(Screen.DestinationPicker)
                    }
                )
            }
            composable<Screen.AllRecents> {
                AllRecentsScreen(
                    snackbarHostState = snackbarHostState, onFileClicked = { file ->
                        if (file.isDirectory) {
                            navController.navigate(Screen.FileBrowser(path = file.path))
                        } else {
                            openFile(context, file)
                        }
                    }, onCopyTo = {
                        destinationPickerViewModel.addSourcePath(it.path)
                        destinationPickerViewModel.isCopyFile()
                        navController.navigate(Screen.DestinationPicker)
                    },
                    onMoveTo = {
                        destinationPickerViewModel.addSourcePath(it.path)
                        destinationPickerViewModel.isMoveFile()
                        navController.navigate(Screen.DestinationPicker)
                    }
                )
            }

            navigation<Screen.DestinationPicker>(
                startDestination = Screen.DestinationScreen(
                    destinationPickerViewModel.uiState.value.currPath
                )
            ) {
                composable<Screen.DestinationScreen> { backStackEntry ->
                    val folderPath = backStackEntry.toRoute<Screen.DestinationScreen>().folderPath

                    DestinationScreen(
                        folderPath = folderPath,
                        destinationPickerViewModel,
                        onFolderClicked = {
                            destinationPickerViewModel.updateCurrentPath(it.path)
                            navController.navigate(Screen.DestinationScreen(it.path))
                        },
                        snackbarHostState = snackbarHostState,
                        onNavigateBack = {
                            navController.popBackStack(
                                Screen.DestinationPicker, inclusive = true
                            )
                        }
                    )
                }
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
                    containerColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceContainer else Color.Gray.copy(
                        alpha = 0.1f
                    )
                ),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }

        }
    )
}

@Composable
fun BottomBar(
    onCreateFolder: () -> Unit,
    buttonLabel: String,
    onConfirmLocation: () -> Unit
) {
    BottomAppBar {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { onCreateFolder() },
            ) {
                Icon(
                    modifier = Modifier.padding(end = ButtonDefaults.IconSpacing),
                    imageVector = Icons.Default.CreateNewFolder,
                    contentDescription = null
                )
                Text("Create New Folder")
            }
            Button(onClick = { onConfirmLocation() }) {
                Text(buttonLabel)
            }
        }
    }
}

@Preview
@Composable
fun FabPreview() {
    BottomBar(onCreateFolder = {}, onConfirmLocation = {}, buttonLabel = "Copy Here")
}

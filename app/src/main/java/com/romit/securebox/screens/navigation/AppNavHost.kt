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
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.toRoute
import com.romit.securebox.screens.AllRecentsScreen
import com.romit.securebox.screens.DestinationScreen
import com.romit.securebox.screens.FileBrowserScreen
import com.romit.securebox.screens.HomeScreen
import com.romit.securebox.util.openFile
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
    val isDestinationScreen = currentBackStackEntry?.destination?.hasRoute<Screen.DestinationScreen>() == true
    val sharedFileBrowserViewModel: FileBrowserScreenViewModel = hiltViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by sharedFileBrowserViewModel.uiState.collectAsState()

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
            if (isDestinationScreen && uiState.selectedFile?.path != null) {
                BottomBar(
                    onCreateFolder = { sharedFileBrowserViewModel.toggleCreateFolderDialog() },
                    onConfirmLocation = {
                        if (uiState.selectedFile!!.path.isNotBlank()) {
                            if (uiState.isCopyFile) {
                                sharedFileBrowserViewModel.copyFile(
                                    uiState.selectedFile!!.path,
                                    uiState.operationTargetPath
                                )
                            } else {
                                sharedFileBrowserViewModel.moveFile(
                                    uiState.selectedFile!!.path,
                                    uiState.operationTargetPath
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
                    buttonLabel = if (uiState.isCopyFile) "Copy Here" else "Move Here"
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
                    viewModel = sharedFileBrowserViewModel,
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
                    onCopyTo = { // ✅ No parameter
                        sharedFileBrowserViewModel.isCopyFile()
                        navController.navigate(Screen.DestinationScreen(
                            sharedFileBrowserViewModel.uiState.value.operationTargetPath
                        ))
                    },
                    onMoveTo = { // ✅ No parameter
                        sharedFileBrowserViewModel.isMoveFile()
                        navController.navigate(Screen.DestinationScreen(
                            sharedFileBrowserViewModel.uiState.value.operationTargetPath
                        ))
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
                        sharedFileBrowserViewModel.isCopyFile()
                        navController.navigate(Screen.DestinationScreen(sharedFileBrowserViewModel.uiState.value.operationTargetPath))
                    },
                    onMoveTo = {
                        sharedFileBrowserViewModel.isMoveFile()
                        navController.navigate(Screen.DestinationScreen(sharedFileBrowserViewModel.uiState.value.operationTargetPath))
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
                        sharedFileBrowserViewModel.isCopyFile()
                        navController.navigate(Screen.DestinationScreen(sharedFileBrowserViewModel.uiState.value.operationTargetPath))
                    },
                    onMoveTo = {
                        sharedFileBrowserViewModel.isMoveFile()
                        navController.navigate(Screen.DestinationScreen(sharedFileBrowserViewModel.uiState.value.operationTargetPath))
                    }
                )
            }


            composable<Screen.DestinationScreen> { backStackEntry ->
                val folderPath = backStackEntry.toRoute<Screen.DestinationScreen>().folderPath

                DestinationScreen(
                    folderPath = folderPath,
                    sharedFileBrowserViewModel,
                    onFolderClicked = {
                        sharedFileBrowserViewModel.updateCurrentPath(it.path)
                        navController.navigate(Screen.DestinationScreen(it.path))
                    },
                    snackbarHostState = snackbarHostState,
                    onNavigateBack = {
                        navController.popBackStack()
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

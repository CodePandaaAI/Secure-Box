package com.romit.securebox.screens.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.romit.securebox.screens.FileBrowserScreen
import com.romit.securebox.screens.HomeScreen


@Composable
fun SecureApp() {
    val navController = rememberNavController()
    AppNavHost(navController)
}

@Composable
fun AppNavHost(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val context = LocalContext.current

    val isHomeScreen = currentBackStackEntry?.destination?.route == Screen.Home::class.qualifiedName

    BackHandler(enabled = isHomeScreen) {
        // When on home screen, system back button exits app
        (context as? Activity)?.finish()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                currentBackStackEntryProvider = { currentBackStackEntry },
                onBackClick = {
                    if (isHomeScreen) {
                        // Exit app when on home screen
                        (context as? Activity)?.finish()
                    } else {
                        // Navigate back when on other screens
                        navController.popBackStack()
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = Screen.Home
        ) {
            composable<Screen.Home> {
                HomeScreen(
                    onCategoryClicked = { path ->
                        navController.navigate(Screen.FileBrowser(path))
                    }
                )
            }

            composable<Screen.FileBrowser> { backStackEntry ->
                val path = backStackEntry.toRoute<Screen.FileBrowser>().path
                FileBrowserScreen(
                    path = path,
                    onFileClicked = { file ->
                        if (file.isDirectory) navController.navigate(Screen.FileBrowser(path = file.path))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentBackStackEntryProvider: () -> NavBackStackEntry?,  // ✅ Lambda parameter
    onBackClick: () -> Unit
) {
    // ✅ Read the state HERE in the child
    val currentBackStackEntry = currentBackStackEntryProvider()
    val isHomeScreen = currentBackStackEntry?.destination?.route == Screen.Home::class.qualifiedName

    CenterAlignedTopAppBar(
        title = { Text("Secure Box") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = if (isHomeScreen) "Exit app" else "Go back"
                )
            }
        }
    )
}
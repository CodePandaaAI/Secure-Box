package com.romit.securebox.screens.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.romit.securebox.screens.FileBrowserScreen
import com.romit.securebox.screens.HomeScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppTopBar(onBackClick = { navController.popBackStack() }) }) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = Screen.Home
        ) {
            composable<Screen.Home> {
                HomeScreen(onCategoryClicked = { navController.navigate(Screen.FileBrowser(it)) })
            }

            composable<Screen.FileBrowser> { backStackEntry ->
                val path = backStackEntry.toRoute<Screen.FileBrowser>().path
                FileBrowserScreen(path = path)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(title = { Text("Secure Box") }, navigationIcon = {
        IconButton(onClick = { onBackClick() }) {
            Icon(Icons.Default.ArrowBackIosNew, contentDescription = null)
        }
    })
}
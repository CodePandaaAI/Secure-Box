package com.romit.securebox.components.app

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.romit.securebox.navigation.Screen
import com.romit.securebox.presentation.sharedViewmodel.NavigationViewModel

/**
 * A composable function that displays the top app bar for the application.
 * It includes a title and a back navigation button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
) {
    val activity = LocalActivity.current as ComponentActivity
    val navigationViewModel = hiltViewModel<NavigationViewModel>(viewModelStoreOwner = activity)
    val currentScreen = navigationViewModel.currentScreen()

    if (currentScreen != null && currentScreen !is Screen.Home) {
        TopAppBar(
            title = { Text("Secure Box") },
            navigationIcon = {
                IconButton(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    onClick = { navigationViewModel.removeLastOrNull() },
                    colors = IconButtonDefaults.iconButtonColors(
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
}
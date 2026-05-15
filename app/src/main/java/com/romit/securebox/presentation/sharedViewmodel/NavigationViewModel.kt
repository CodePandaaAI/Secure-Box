package com.romit.securebox.presentation.sharedViewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.romit.securebox.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {
    private val _backStack: SnapshotStateList<Screen> = mutableStateListOf(Screen.Home)

    val backStack: List<Screen> = _backStack

    fun currentScreen(): Screen? = backStack.lastOrNull()

    fun navigateTo(screen: Screen) {
        _backStack.add(screen)
    }

    fun removeLastOrNull() {
        Log.d(
            "Current Back Stack Before Removal",
            backStack.toString()
        )
        if (_backStack.size > 1) {
            _backStack.removeAt(_backStack.lastIndex)
            Log.d(
                "Current Back Stack After Removal",
                backStack.toString()
            )
        }
    }

    fun removeIf(predicate: (Screen) -> Boolean) {
        _backStack.removeAll(predicate)
    }

    fun navigateToFolder(
        currentScreen: Screen.DestinationScreen,
        screen: Screen.DestinationScreen
    ) {
        // Add current path to history before navigating
        if (currentScreen.folderPath.isNotEmpty() && backStack.lastOrNull() != screen) {
            navigateTo(screen)
        }
    }
}
package com.romit.securebox.presentation.sharedViewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.romit.securebox.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(): ViewModel() {
    private val _backStack: SnapshotStateList<Screen> = mutableStateListOf(Screen.Home)

    val backStack: List<Screen> = _backStack

    val currentScreen: Screen?
        get() = _backStack.lastOrNull()

    fun navigateTo(screen: Screen) {
        _backStack.add(screen)
    }

    fun removeLastOrNull() {
        if (_backStack.size > 1) {
            _backStack.removeAt(_backStack.size - 1)
        }
    }

    fun removeIf(predicate: (Screen) -> Boolean) {
        _backStack.removeAll(predicate)
    }
}
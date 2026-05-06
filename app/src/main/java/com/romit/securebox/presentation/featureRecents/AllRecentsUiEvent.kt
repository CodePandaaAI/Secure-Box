package com.romit.securebox.presentation.featureRecents

sealed interface AllRecentsUiEvent {
    data class ShowSnackBar(val message: String) : AllRecentsUiEvent
}
package com.romit.securebox.presentation.sharedViewmodel

sealed interface SharedFileOperationsUiEvent {
    data class ShowSnackBar(val message: String): SharedFileOperationsUiEvent
}
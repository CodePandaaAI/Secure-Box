package com.romit.securebox.presentation.viewmodel

sealed interface SharedFileOperationsUiEvent {
    data class ShowSnackBar(val message: String): SharedFileOperationsUiEvent
}
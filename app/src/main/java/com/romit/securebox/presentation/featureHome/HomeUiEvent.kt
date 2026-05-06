package com.romit.securebox.presentation.featureHome

sealed interface HomeUiEvent {
    data class ShowSnackBar(val message: String) : HomeUiEvent
}
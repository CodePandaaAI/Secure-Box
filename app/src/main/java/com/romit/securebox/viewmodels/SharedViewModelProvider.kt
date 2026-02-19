package com.romit.securebox.viewmodels

import androidx.compose.runtime.staticCompositionLocalOf

val SharedViewModelProvider = staticCompositionLocalOf<SharedFileOperationsViewModel> {
    error("No SharedViewModel provided")
}
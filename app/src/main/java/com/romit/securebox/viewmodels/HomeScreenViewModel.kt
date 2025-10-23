package com.romit.securebox.viewmodels

import android.R.attr.path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.model.HomeUiState
import com.romit.securebox.data.repository.FileRepository
import com.romit.securebox.util.StorageHelper
import com.romit.securebox.util.StorageHelper.getStorageCategories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel : ViewModel() {
    private var _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    val repo = FileRepository()

    init {
        getStorageCategories()
        getRecentFiles()
    }

    fun getRecentFiles() {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null, isLoading = true) }
            try {
                val recentFiles = repo.getRecentFiles(limit = 6)
                _uiState.update { it.copy(recentFiles = recentFiles, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun getStorageCategories() {
        try {
            val storageCategoriesList = StorageHelper.getStorageCategories()
            _uiState.update { it.copy(storageCategoriesList = storageCategoriesList) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        }
    }
}
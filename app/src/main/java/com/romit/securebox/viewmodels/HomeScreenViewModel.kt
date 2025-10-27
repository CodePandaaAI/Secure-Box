package com.romit.securebox.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.model.HomeUiState
import com.romit.securebox.data.repository.FileRepository
import com.romit.securebox.util.StorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repository: FileRepository) :
    ViewModel() {
    private var _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getStorageCategories()
        getRecentFiles()
    }

    fun getRecentFiles() {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null, isLoading = true) }
            try {
                val recentFiles = repository.getRecentFiles(limit = 6)
                _uiState.update { it.copy(recentFiles = recentFiles, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun getStorageCategories() {
        viewModelScope.launch {
            try {
                val storageCategoriesList = StorageHelper.getStorageCategories().map { dir ->
                    dir.copy(dirSize = repository.getDirectorySize(dir.path))
                }
                _uiState.update { it.copy(storageCategoriesList = storageCategoriesList) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
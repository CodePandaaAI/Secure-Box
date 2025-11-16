package com.romit.securebox.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.model.HomeUiState
import com.romit.securebox.data.repository.FileRepository
import com.romit.securebox.util.StorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repository: FileRepository) : ViewModel() {

    private var _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getStorageCategories()
        getRecentFiles()
    }
    fun getRecentFiles() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null, isRefreshing = true) }
            try {
                val recentFiles = repository.getRecentFiles(limit = 4)
                _uiState.update { it.copy(recentFilesList = recentFiles, isRefreshing = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message, isRefreshing = false) }
            }
        }
    }

    fun getStorageCategories() {
        viewModelScope.launch {
            try {
                val categories = StorageHelper.getStorageCategories()
                _uiState.update { it.copy(storageCategories = categories) }

                val categoriesWithSizes = withContext(Dispatchers.IO) {
                    categories.map { dir ->
                        async {
                            dir.copy(dirSize = repository.getDirectorySize(dir.path))
                        }
                    }.awaitAll()
                }
                _uiState.update { it.copy(storageCategories = categoriesWithSizes) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
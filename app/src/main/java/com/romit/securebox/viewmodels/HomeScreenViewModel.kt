package com.romit.securebox.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.model.FileItem
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
                val recentFiles = repository.getRecentFiles(limit = 4)
                _uiState.update { it.copy(recentFiles = recentFiles, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun getStorageCategories() {
        viewModelScope.launch {
            try {
                val categories = StorageHelper.getStorageCategories()
                _uiState.update { it.copy(storageCategoriesList = categories) }

                val categoriesWithSizes = withContext(Dispatchers.IO) {
                    categories.map { dir ->
                        async {  // Each size calculated in parallel!
                            dir.copy(dirSize = repository.getDirectorySize(dir.path))
                        }
                    }.awaitAll()
                }
                _uiState.update { it.copy(storageCategoriesList = categoriesWithSizes) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteFile(filePath: String) {
        viewModelScope.launch {
            try {
                repository.deleteFile(filePath)
            } catch (e: Exception) {

            }
        }
    }

    fun toggleDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = !uiState.value.showDeleteDialog) }
    }

    fun selectedFileForBottomSheet(file: FileItem?) {
        _uiState.update { it.copy(selectedFile = file) }
    }
}
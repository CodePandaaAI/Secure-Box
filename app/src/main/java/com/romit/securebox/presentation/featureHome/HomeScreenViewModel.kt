package com.romit.securebox.presentation.featureHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.repository.FileRepository
import com.romit.securebox.data.model.StorageCategoryType
import com.romit.securebox.util.StorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repository: FileRepository) :
    ViewModel() {

    private var _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.RecentFilesLoading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEvent =  Channel<HomeUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _storageCategories = MutableStateFlow(StorageHelper.getStorageCategories())
    val storageCategories = _storageCategories.asStateFlow()

    init {
        getStorageCategories()
        getRecentFiles()
    }

    fun getRecentFiles() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.RecentFilesLoading
            try {
                val recentFiles = repository.getRecentFiles(limit = 9)
                _uiState.value = HomeUiState.Success(recentFilesList = recentFiles)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Something went wrong")
                _uiEvent.send(HomeUiEvent.ShowSnackBar(e.message ?: "Something went wrong"))
            }
        }
    }

    fun getStorageCategories() {
        viewModelScope.launch {
            try {
                val categoriesListWithSizes = withContext(Dispatchers.IO) {
                    _storageCategories.value.map { dir ->
                        async {
                            when (dir.type) {
                                StorageCategoryType.DOWNLOADS,
                                StorageCategoryType.INTERNAL_STORAGE -> {
                                    dir.copy(dirSize = repository.getDirectorySize(dir.path))
                                }

                                else -> dir
                            }
                        }
                    }.awaitAll()
                }

                _storageCategories.value = categoriesListWithSizes
            } catch (e: Exception) {
                _uiEvent.send(HomeUiEvent.ShowSnackBar(e.message ?: "Something went wrong"))
            }
        }
    }
}

package com.romit.securebox.presentation.featureHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.repository.FileRepository
import com.romit.securebox.util.StorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repository: FileRepository) :
    ViewModel() {

    private var _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.RecentFilesLoading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<HomeUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    val storageCategories = StorageHelper.getStorageCategories()

    init {
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
}

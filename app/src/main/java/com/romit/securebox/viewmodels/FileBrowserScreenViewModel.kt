package com.romit.securebox.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.model.FileBrowserUiState
import com.romit.securebox.data.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileBrowserScreenViewModel @Inject constructor(private val repository: FileRepository) : ViewModel() {
    private var _uiState = MutableStateFlow(FileBrowserUiState())
    val uiState = _uiState.asStateFlow()

    fun getDirFiles(path: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null, isLoading = true) }
            try {
                val files = repository.getDirFileItems(path)

                _uiState.update {
                    it.copy(dirFiles = files, error = null, isLoading = false)
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}
package com.romit.securebox.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.model.DestinationPickerUiState
import com.romit.securebox.data.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DestinationPickerViewModel @Inject constructor(private val repository: FileRepository) :
    ViewModel() {
    private var _uiState = MutableStateFlow(DestinationPickerUiState())
    val uiState = _uiState.asStateFlow()

    fun getDirs(dirPath: String) {
        viewModelScope.launch {

            _uiState.update { it.copy(error = null, success = null, isLoading = true) }

            try {
                val files = repository.getDirs(path = dirPath)
                _uiState.update {
                    it.copy(
                        error = null,
                        success = null,
                        isLoading = false,
                        directories = files
                    )
                }

            } catch (e: Exception) {

                _uiState.update { it.copy(error = e.message, success = null, isLoading = false) }

            }
        }
    }

    fun updateCurrentPath(newCurrPath: String) {
        _uiState.update { it.copy(currPath = newCurrPath) }
    }

    fun copyFile(filePath: String, destPath: String) {
        viewModelScope.launch {
            repository.copyFile(filePath, destPath).fold(
                onSuccess = { message ->
                    _uiState.update {
                        it.copy(
                            success = message,
                            error = null
                        )
                    }
                },
                onFailure = { message ->
                    val error = message.message ?: when (message) {
                        is FileNotFoundException -> "File not found"
                        is FileAlreadyExistsException -> "File already exists"
                        is SecurityException -> "Permission denied"
                        is IOException -> "Copy failed"
                        else -> "Unknown error"
                    }
                    _uiState.update { it.copy(error = error, success = null) }
                }
            )
        }
    }
    fun clearMessages() {
        _uiState.update { it.copy(error = null, success = null) }
    }

    fun addSourcePath(path: String){
        _uiState.update { it.copy(sourcePath = path) }
    }
}
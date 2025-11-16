package com.romit.securebox.viewmodels

import androidx.lifecycle.ViewModel
import com.romit.securebox.data.model.SharedFileOperationsUiState
import com.romit.securebox.data.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedFileOperationsViewModel @Inject constructor(val repository: FileRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SharedFileOperationsUiState())
    private val uiState = _uiState.asStateFlow()

    suspend fun deleteFile(filePath: String): Result<String> {
        return repository.deleteFile(filePath)
    }
}
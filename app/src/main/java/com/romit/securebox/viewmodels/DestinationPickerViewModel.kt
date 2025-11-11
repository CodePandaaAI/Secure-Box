package com.romit.securebox.viewmodels

import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.romit.securebox.data.model.DestinationPickerUiState
import com.romit.securebox.data.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DestinationPickerViewModel @Inject constructor(private val repository: FileRepository) :
    ViewModel() {
    private var _uiState = MutableStateFlow(DestinationPickerUiState())
    val uiState = _uiState.asStateFlow()


    suspend fun getDirs(path: String) {
        viewModelScope.launch {

            _uiState.update { it.copy(error = null, success = null, isLoading = true) }

            try {
                repository.getDirs(path = path)
                _uiState.update { it.copy(error = null, success = null, isLoading = false) }

            } catch (e: Exception) {

                _uiState.update { it.copy(error = e.message, success = null, isLoading = false) }

            }
        }
    }
}
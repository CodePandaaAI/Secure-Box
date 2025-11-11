package com.romit.securebox.viewmodels

import android.R.attr.path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun getDirs() {
        viewModelScope.launch {

            _uiState.update { it.copy(error = null, success = null, isLoading = true) }

            try {
                val files = repository.getDirs(path = uiState.value.currPath)
                _uiState.update { it.copy(error = null, success = null, isLoading = false, directories = files) }

            } catch (e: Exception) {

                _uiState.update { it.copy(error = e.message, success = null, isLoading = false) }

            }
        }
    }
}
package com.romit.securebox.presentation.featureRecents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllRecentsScreenViewModel @Inject constructor(private val repository: FileRepository) :
    ViewModel() {
    private val _uiState: MutableStateFlow<AllRecentsUiState> =
        MutableStateFlow(AllRecentsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<AllRecentsUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    private val pageSize = 30

    init {
        reloadRecentFiles()
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState !is AllRecentsUiState.Success) return
        if (currentState.isLoadingNextPage || currentState.isPaginationEndReached) return
        val lastTimestamp = currentState.allRecents.lastOrNull()?.lastModified

        if (lastTimestamp == null) {
            reloadRecentFiles()
            return
        }
        viewModelScope.launch {
            // Safe update: set loading
            _uiState.update { state ->
                if (state is AllRecentsUiState.Success) state.copy(isLoadingNextPage = true) else state
            }

            try {
                val newFiles = repository.getRecentFiles(lastTimestamp, pageSize)

                // Safe update: append files
                _uiState.update { state ->
                    if (state is AllRecentsUiState.Success) {
                        state.copy(
                            allRecents = state.allRecents + newFiles,
                            isLoadingNextPage = false,
                            isPaginationEndReached = newFiles.size < pageSize
                        )
                    } else state
                }
            } catch (e: Exception) {
                // Fix deadlock: reset loading on error
                _uiState.update { state ->
                    if (state is AllRecentsUiState.Success) state.copy(isLoadingNextPage = false) else state
                }
                _uiEvent.send(
                    AllRecentsUiEvent.ShowSnackBar(e.message ?: "Something went wrong")
                )
            }
        }
    }
    fun reloadRecentFiles() {
        if (uiState.value is AllRecentsUiState.Success) {
            if ((uiState.value as AllRecentsUiState.Success).isLoadingNextPage) return
        }
        viewModelScope.launch {
            _uiState.value = AllRecentsUiState.Loading
            try {
                val newFiles = repository.getRecentFiles(lastTimestamp = null, pageSize = pageSize)

                _uiState.value = AllRecentsUiState.Success(
                    allRecents = newFiles,
                    isPaginationEndReached = newFiles.size < pageSize
                )

            } catch (e: Exception) {
                _uiEvent.send(AllRecentsUiEvent.ShowSnackBar(e.message ?: "Something went wrong"))
            }
        }

    }
}
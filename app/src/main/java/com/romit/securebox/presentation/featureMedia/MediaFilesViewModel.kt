package com.romit.securebox.presentation.featureMedia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.model.StorageCategoryType
import com.romit.securebox.data.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaFilesViewModel @Inject constructor(
    private val repository: FileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MediaFilesUiState>(MediaFilesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val pageSize = 30

    fun reload(type: StorageCategoryType) {
        viewModelScope.launch {
            _uiState.value = MediaFilesUiState.Loading
            try {
                val files = repository.getMediaFiles(
                    categoryType = type,
                    lastTimestamp = null,
                    pageSize = pageSize
                )
                _uiState.value = MediaFilesUiState.Success(
                    files = files,
                    isPaginationEndReached = files.size < pageSize
                )
            } catch (e: Exception) {
                _uiState.value = MediaFilesUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun loadNextPage(type: StorageCategoryType) {
        val currentState = _uiState.value
        if (currentState !is MediaFilesUiState.Success) return
        if (currentState.isLoadingNextPage || currentState.isPaginationEndReached) return

        val lastTimestamp = currentState.files.lastOrNull()?.lastModified
        if (lastTimestamp == null) {
            reload(type)
            return
        }

        viewModelScope.launch {
            _uiState.update { state ->
                if (state is MediaFilesUiState.Success) {
                    state.copy(isLoadingNextPage = true)
                } else {
                    state
                }
            }

            try {
                val nextFiles = repository.getMediaFiles(
                    categoryType = type,
                    lastTimestamp = lastTimestamp,
                    pageSize = pageSize
                )

                _uiState.update { state ->
                    if (state is MediaFilesUiState.Success) {
                        state.copy(
                            files = state.files + nextFiles,
                            isLoadingNextPage = false,
                            isPaginationEndReached = nextFiles.size < pageSize
                        )
                    } else {
                        state
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    if (state is MediaFilesUiState.Success) {
                        state.copy(isLoadingNextPage = false)
                    } else {
                        state
                    }
                }
            }
        }
    }
}

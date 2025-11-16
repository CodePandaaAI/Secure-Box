package com.romit.securebox.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.model.FileBrowserUiState
import com.romit.securebox.data.repository.FileRepository
import com.romit.securebox.util.StorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FileBrowserScreenViewModel @Inject constructor(private val repository: FileRepository) :
    ViewModel() {
    private var _uiState = MutableStateFlow(FileBrowserUiState())
    val uiState = _uiState.asStateFlow()

    private var currentLoadJob: Job? = null

    // ========== EXISTING FILE BROWSER FUNCTIONS ==========

    fun getDirFiles(path: String) {
        currentLoadJob?.cancel()

        _uiState.update { it.copy(browsingPath = path, errorMessage = null, isLoading = true) }
        currentLoadJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val files = repository.getDirFileItems(path)
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(browsingPathDirectories = files, errorMessage = null, isLoading = false)
                    }
                }
                val dirWithSize = files.map { file ->
                    async {
                        if (file.isDirectory) {
                            val dirSize = StorageHelper.getDirectorySize(File(file.path))
                            file.copy(size = StorageHelper.formatSize(dirSize))
                        } else {
                            file
                        }
                    }
                }.awaitAll()

                if (isActive) {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(browsingPathDirectories = dirWithSize, errorMessage = null, isLoading = false)
                        }
                    }
                }

            } catch (e: CancellationException) {
                // Ignore cancellation
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
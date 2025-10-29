package com.romit.securebox.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.model.FileBrowserUiState
import com.romit.securebox.data.model.FileItem
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

    fun getDirFiles(path: String) {
        currentLoadJob?.cancel()

        _uiState.update { it.copy(error = null, isLoading = true) }
        currentLoadJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val files = repository.getDirFileItems(path)
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(dirFiles = files, error = null, isLoading = false)
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

                // Step 3: Update UI with sizes
                // ✅ Check if still active before updating
                if (isActive) {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(dirFiles = dirWithSize, error = null, isLoading = false)
                        }
                    }
                }

            } catch (e: CancellationException) {

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
            }
        }
    }

    fun toggleShowBottomSheet() {
        _uiState.update { it.copy(showBottomSheet = !uiState.value.showBottomSheet) }
    }

    fun selectedFileForBottomSheet(file: FileItem?) {
        _uiState.update { it.copy(selectedFile = file) }
    }
}
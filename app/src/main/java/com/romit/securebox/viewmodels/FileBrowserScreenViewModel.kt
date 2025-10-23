package com.romit.securebox.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romit.securebox.data.model.FileBrowserUiState
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.data.repository.FileRepository
import com.romit.securebox.util.StorageHelper.getMimeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FileBrowserScreenViewModel() : ViewModel() {
    private var _uiState = MutableStateFlow(FileBrowserUiState())
    val uiState = _uiState.asStateFlow()
    val repo = FileRepository()

    fun getDirFiles(path: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null, isLoading = true) }
            try {
                val files = repo.getDirFiles(path)
                val fileItems = files.sortedByDescending { it.lastModified() }.map { file ->
                    FileItem(
                        path = file.absolutePath,
                        name = file.name,
                        isDirectory = file.isDirectory,
                        size = file.length(),
                        lastModified = file.lastModified(),
                        mimeType = getMimeType(file),
                        extension = file.extension
                    )
                }
                _uiState.update {
                    it.copy(dirFiles = fileItems, error = null, isLoading = false)
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}
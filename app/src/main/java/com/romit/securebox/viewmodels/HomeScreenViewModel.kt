package com.romit.securebox.viewmodels

import androidx.lifecycle.ViewModel
import com.romit.securebox.data.model.StorageCategory
import com.romit.securebox.util.StorageHelper

class HomeScreenViewModel : ViewModel() {
    fun getStorageCategories(): List<StorageCategory> {
        return StorageHelper.getStorageCategories()
    }
}
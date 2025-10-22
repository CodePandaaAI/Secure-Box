package com.romit.securebox.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.romit.securebox.data.model.StorageCategory
import com.romit.securebox.viewmodels.HomeScreenViewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: HomeScreenViewModel = viewModel()) {
    val categoriesList = remember { viewModel.getStorageCategories() }
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StorageCategoryUi(categoriesList[0], modifier = Modifier.padding(horizontal = 8.dp))
            StorageCategoryUi(categoriesList[1], modifier = Modifier.padding(horizontal = 8.dp))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StorageCategoryUi(categoriesList[2], modifier = Modifier.padding(horizontal = 8.dp))
            StorageCategoryUi(categoriesList[3], modifier = Modifier.padding(horizontal = 8.dp))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StorageCategoryUi(categoriesList[4], modifier = Modifier.padding(horizontal = 8.dp))
            StorageCategoryUi(categoriesList[5], modifier = Modifier.padding(horizontal = 8.dp))
        }
    }
}

@Composable
fun StorageCategoryUi(category: StorageCategory, modifier: Modifier) {
    Surface(shape = RoundedCornerShape(16.dp)) {
        Column(modifier = modifier) {
            Text(category.name, style = MaterialTheme.typography.titleSmall)
        }
    }
}
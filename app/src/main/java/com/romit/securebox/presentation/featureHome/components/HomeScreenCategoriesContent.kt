package com.romit.securebox.presentation.featureHome.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.romit.securebox.components.storage.StorageCategoryCard
import com.romit.securebox.data.model.StorageCategory

@Composable
fun HomeScreenCategoriesContent(storageCategories: () -> List<StorageCategory>, onCategoryClicked: (StorageCategory) -> Unit) {

    val storageCategories = storageCategories()

    Text(
        text = "Categories",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )

    if (storageCategories.isEmpty()) {
        HomeLoadingScreen()
    } else {
        // Category Grid (cleaner with Column for rows)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StorageCategoryCard(
                    storageCategories[0],
                    onCategoryClick = onCategoryClicked,
                    modifier = Modifier.weight(1f)
                )
                StorageCategoryCard(
                    storageCategories[1],
                    onCategoryClick = onCategoryClicked,
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 2
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StorageCategoryCard(
                    storageCategories[2],
                    onCategoryClick = onCategoryClicked,
                    modifier = Modifier.weight(1f)
                )
                StorageCategoryCard(
                    storageCategories[3],
                    onCategoryClick = onCategoryClicked,
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 3
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StorageCategoryCard(
                    storageCategories[4],
                    onCategoryClick = onCategoryClicked,
                    modifier = Modifier.weight(1f)
                )
                StorageCategoryCard(
                    storageCategories[5],
                    onCategoryClick = onCategoryClicked,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

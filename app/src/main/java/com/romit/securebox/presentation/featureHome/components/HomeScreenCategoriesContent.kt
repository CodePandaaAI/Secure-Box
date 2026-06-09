package com.romit.securebox.presentation.featureHome.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.romit.securebox.components.storage.StorageCategoryCard
import com.romit.securebox.data.model.StorageCategory

@Composable
fun HomeScreenCategoriesContent(
    storageCategories: () -> List<StorageCategory>,
    onCategoryClicked: (StorageCategory) -> Unit
) {

    val storageCategories = storageCategories()
    Box(
        Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 4.dp
                )
            )
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }

    if (storageCategories.isEmpty()) {
        HomeScreenLoadingView()
    } else {
        // Category Grid (cleaner with Column for rows)
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Row 1
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                StorageCategoryCard(
                    storageCategories[0],
                    onCategoryClick = onCategoryClicked,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 4.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 4.dp
                    )
                )
                StorageCategoryCard(
                    storageCategories[1],
                    onCategoryClick = onCategoryClicked,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 4.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 4.dp
                    )
                )
            }

            // Row 2
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                StorageCategoryCard(
                    storageCategories[2],
                    onCategoryClick = onCategoryClicked,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
                )
                StorageCategoryCard(
                    storageCategories[3],
                    onCategoryClick = onCategoryClicked,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
                )
            }

            // Row 3
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                StorageCategoryCard(
                    storageCategories[4],
                    onCategoryClick = onCategoryClicked,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                StorageCategoryCard(
                    storageCategories[5],
                    onCategoryClick = onCategoryClicked,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 24.dp, bottomEnd = 24.dp)
                )
            }
        }
    }

}

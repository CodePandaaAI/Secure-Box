package com.romit.securebox.presentation.featureHome.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.romit.securebox.components.StorageCategoryCard
import com.romit.securebox.presentation.featureHome.HomeUiState
import com.romit.securebox.ui.theme.CustomFontFamily

@Composable
fun HomeScreenCategoriesContent(uiState: HomeUiState, onCategoryClicked: (String) -> Unit) {
    Text(
        text = "Categories",
        fontFamily = CustomFontFamily,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
    )

    if (uiState.storageCategories.isEmpty()) {
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
                    uiState.storageCategories[0],
                    onCategoryClick = onCategoryClicked,
                    modifier = Modifier.weight(1f)
                )
                StorageCategoryCard(
                    uiState.storageCategories[1],
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
                    uiState.storageCategories[2],
                    onCategoryClick = onCategoryClicked,
                    modifier = Modifier.weight(1f)
                )
                StorageCategoryCard(
                    uiState.storageCategories[3],
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
                    uiState.storageCategories[4],
                    onCategoryClick = onCategoryClicked,
                    modifier = Modifier.weight(1f)
                )
                StorageCategoryCard(
                    uiState.storageCategories[5],
                    onCategoryClick = onCategoryClicked,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
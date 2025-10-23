package com.romit.securebox.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.romit.securebox.components.FileCard
import com.romit.securebox.data.model.StorageCategory
import com.romit.securebox.viewmodels.HomeScreenViewModel

@Composable
fun HomeScreen(
    onCategoryClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Recent Downloads",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Spacer(Modifier.height(16.dp))
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            uiState.recentFiles.forEach { file ->
                FileCard(file = file, onClick = {})
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "Categories",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Spacer(Modifier.height(16.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StorageCategoryUi(
                uiState.storageCategoriesList[0],
                onClick = { onCategoryClicked(it) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            StorageCategoryUi(
                uiState.storageCategoriesList[1],
                onClick = { onCategoryClicked(it) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StorageCategoryUi(
                uiState.storageCategoriesList[2],
                onClick = { onCategoryClicked(it) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            StorageCategoryUi(
                uiState.storageCategoriesList[3],
                onClick = { onCategoryClicked(it) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StorageCategoryUi(
                uiState.storageCategoriesList[4],
                onClick = { onCategoryClicked(it) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            StorageCategoryUi(
                uiState.storageCategoriesList[5],
                onClick = { onCategoryClicked(it) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun StorageCategoryUi(
    category: StorageCategory, onClick: (String) -> Unit, modifier: Modifier
) {
    Card(
        modifier = modifier.clickable { onClick(category.path) },
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = category.icon, contentDescription = null)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(category.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    category.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

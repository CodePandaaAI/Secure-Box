package com.romit.securebox.presentation.featureHome.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.romit.securebox.components.FileCard
import com.romit.securebox.data.model.FileItem
import com.romit.securebox.presentation.featureHome.HomeUiState
import com.romit.securebox.util.getListItemShape

@Composable
fun HomeScreenRecentsContent(
    uiState: HomeUiState.Success,
    onSelectFileForBottomSheet: (FileItem) -> Unit,
    onShowAllRecents: () -> Unit,
    onOpenFile: (FileItem) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Recents",
            style = MaterialTheme.typography.titleMedium
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable { onShowAllRecents() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Show all",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Show all",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    // Recent Files List
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        uiState.recentFilesList.forEachIndexed { index, file ->
            FileCard(
                file = file,
                onOpenFile = { onOpenFile(it) },
                onSelectFileForBottomSheet = {
                    onSelectFileForBottomSheet(it)
                },
                shape = getListItemShape(
                    index = index,
                    totalItems = uiState.recentFilesList.size
                )
            )
        }
    }
}
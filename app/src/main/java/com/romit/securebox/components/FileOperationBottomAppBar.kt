package com.romit.securebox.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FileOperationBottomAppBar(
    onCreateFolder: () -> Unit,
    buttonLabel: String,
    onConfirmLocation: () -> Unit
) {
    BottomAppBar(
        containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else Color(
            red = 242,
            green = 242,
            blue = 247
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .widthIn(max = 220.dp),
                onClick = onCreateFolder,
            ) {
                Icon(
                    modifier = Modifier.padding(end = ButtonDefaults.IconSpacing),
                    imageVector = Icons.Default.CreateNewFolder,
                    contentDescription = null
                )
                Text("Create New Folder")
            }
            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .widthIn(max = 220.dp)
                    .padding(start = 8.dp), onClick = onConfirmLocation
            ) {
                Text(buttonLabel)
            }
        }
    }
}

@Preview
@Composable
fun FabPreview() {
    FileOperationBottomAppBar(
        onCreateFolder = {},
        onConfirmLocation = {},
        buttonLabel = "Copy Here"
    )
}
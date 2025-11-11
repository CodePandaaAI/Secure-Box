package com.romit.securebox.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.romit.securebox.data.model.FileItem

@Composable
fun RenameDialog(
    onDismissRequest: () -> Unit,
    onRenamingFile: (String) -> Unit,
    onCancel: () -> Unit,
    onRenameFileClicked: () -> Unit,
    newFileName: () -> String,
    selectedFile: () -> FileItem
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                    RoundedCornerShape(28.dp)
                )
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Rename", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = newFileName(),
                onValueChange = { onRenamingFile(it) },
                label = { Text("New name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
                Button(
                    onClick = { onRenameFileClicked() },
                    enabled = newFileName().isNotBlank() &&
                            newFileName() != selectedFile().name
                ) {
                    Text("Save")
                }
            }
        }
    }
}
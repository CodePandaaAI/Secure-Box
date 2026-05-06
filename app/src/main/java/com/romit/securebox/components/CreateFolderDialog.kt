package com.romit.securebox.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CreateFolderDialog(
    folderName: String,
    error: String?,
    onFolderNameChange: (String) -> Unit,
    onConfirmFolderCreation: () -> Unit,
    onDismissFolderDialog: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissFolderDialog,
        title = { Text("Create New Folder") },
        text = {
            Column {
                OutlinedTextField(
                    value = folderName,
                    onValueChange = onFolderNameChange,
                    label = { Text("Folder name") },
                    isError = error != null,
                    supportingText = {
                        if (error != null) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmFolderCreation,
                enabled = folderName.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissFolderDialog) {
                Text("Cancel")
            }
        }
    )
}
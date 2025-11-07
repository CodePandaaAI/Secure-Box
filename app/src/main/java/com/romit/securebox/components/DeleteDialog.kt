package com.romit.securebox.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.romit.securebox.data.model.FileItem

@Composable
fun DeleteDialog(
    onDismissRequest: () -> Unit,
    onConfirmDelete: () -> Unit,
    selectedFile: () -> FileItem
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Delete ${selectedFile().name}?") },
        text = {
            Text(
                if (selectedFile().isDirectory) {
                    "This folder and all its contents will be permanently deleted."
                } else {
                    "This file will be permanently deleted."
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmDelete()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
package com.romit.securebox.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.romit.securebox.ui.theme.SecureBoxTheme

@Preview
@Composable
fun PreviewElements() {
    SecureBoxTheme {
        Scaffold(
            Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {


            Column(
                Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.background
                    ), modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text("I'm, Back", Modifier.padding(32.dp))
                }
                Card(
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.surfaceVariant
                    ), modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 32.dp)
                        .fillMaxWidth()
                ) {
                    Text("I'm, Surface Variant", Modifier.padding(16.dp))
                }
                Card(
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.surface
                    ), modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text("I'm, Surface", Modifier.padding(32.dp))
                }
                Card(
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.primary
                    ), modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text("I'm, Primary", Modifier.padding(32.dp))
                }
                Card(
                    modifier = Modifier
                        .padding(vertical = 32.dp, horizontal = 16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.DarkGray.copy(alpha = 0.2f) // 👈 only fades background
                    )
                ) {
                    Text("I'm, Back", Modifier.padding(32.dp))
                }
                Card(
                    modifier = Modifier
                        .padding(vertical = 32.dp, horizontal = 16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Gray.copy(alpha = 0.1f) // 👈 only fades background
                    )
                ) {
                    Text("I'm, Back", Modifier.padding(32.dp))
                }
            }
        }
    }
}
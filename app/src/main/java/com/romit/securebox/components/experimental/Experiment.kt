package com.romit.securebox.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Card
import androidx.compose.material3.Text

@Preview
@Composable
fun IosUi() {
    val items = listOf("General", "Accessibility", "Privacy & Security", "Passwords")
    Column(Modifier.statusBarsPadding()) {
        items.forEachIndexed { index, item ->
            val shape = when (index) {
                0 -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                items.lastIndex -> RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                else -> RoundedCornerShape(0.dp)
            }
            Card(
                shape = shape,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(shape)
                    .clickable(onClick = {})
            ) {
                Text(
                    text = item,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
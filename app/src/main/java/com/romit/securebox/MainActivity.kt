package com.romit.securebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.romit.securebox.ui.theme.SecureBoxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SecureBoxTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FileScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun FileScreen(modifier: Modifier = Modifier) {

}

@Composable
fun FilePreview(fileName: String, isDirectory: Boolean) {
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SecureBoxTheme {
    }
}
package com.romit.securebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.romit.securebox.screens.navigation.AppNavHost
import com.romit.securebox.ui.theme.SecureBoxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SecureBoxTheme {
                AppNavHost()
            }
        }
    }
}
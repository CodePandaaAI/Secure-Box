package com.romit.securebox

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.romit.securebox.screens.navigation.SecureApp
import com.romit.securebox.ui.theme.CustomFontFamily
import com.romit.securebox.ui.theme.SecureBoxTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main entry point of the application.
 *
 * This activity is responsible for handling the initial setup, including checking and requesting
 * necessary permissions before launching the main user interface. It ensures that the app has
 * "All Files Access" permission, which is critical for its file management functionalities.
 *
 * If the permission is already granted, it displays the main app content via the [SecureApp] composable.
 * If the permission is not granted, it launches the system settings screen to allow the user
 * to grant the permission. A loading/requesting message is shown in the meantime.
 *
 * This class is annotated with `@AndroidEntryPoint` to enable dependency injection via Hilt.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SecureBoxTheme {
                var hasExternalStoragePermission by remember { mutableStateOf(Environment.isExternalStorageManager()) }

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) {
                    // Check after user returns from settings
                    hasExternalStoragePermission = Environment.isExternalStorageManager()
                }

                if (hasExternalStoragePermission) {
                    SecureApp()
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "SecureBox needs storage permission.",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = CustomFontFamily,
                                fontSize = 24.sp,
                                lineHeight = 30.sp,
                                letterSpacing = 1.8.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Button(
                                modifier = Modifier
                                    .height(60.dp)
                                    .widthIn(max = 220.dp)
                                    .padding(start = 8.dp), onClick = {
                                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                                    launcher.launch(intent)
                                }
                            ) {
                                Text("Grant Storage Permission")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBox() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SecureBox needs storage permission.",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
                fontFamily = CustomFontFamily,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                letterSpacing = 1.8.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Button(
                modifier = Modifier
                    .height(60.dp)
                    .widthIn(max = 220.dp)
                    .padding(start = 8.dp), onClick = { }
            ) {
                Text("Grant Storage Permission")
            }
        }
    }
}
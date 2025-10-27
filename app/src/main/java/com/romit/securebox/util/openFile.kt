package com.romit.securebox.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.widget.Toast
import androidx.core.content.FileProvider
import com.romit.securebox.data.model.FileItem
import java.io.File

fun openFile(context: Context, file: FileItem) {
    try {


        val fileToView = File(file.path)
        if (!fileToView.exists()) {
            Toast.makeText(context, "file not Found", Toast.LENGTH_SHORT).show()
        }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", fileToView)

        val intent = Intent(ACTION_VIEW).apply {
            setDataAndType(uri, file.mimeType ?: "*/*")

            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }


        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Specific exception for when no app can handle the intent
        Toast.makeText(
            context,
            "No app found to open this file",
            Toast.LENGTH_SHORT
        ).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Error opening file: ${e.message}",
            Toast.LENGTH_SHORT
        ).show()
    }
}
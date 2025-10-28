package com.romit.securebox.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.romit.securebox.data.model.FileItem
import java.io.File

fun openFile(context: Context, file: FileItem) {
    try {
        val fileToView = File(file.path)
        if (!fileToView.exists()) {
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            fileToView
        )

        // Special handling for APK files
        if (file.mimeType == "application/vnd.android.package-archive") {
            installApk(context, uri)
            return
        }

        // Regular file opening for other types
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, file.mimeType ?: "*/*")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
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

private fun installApk(context: Context, uri: Uri) {
    // Check if app has permission to install packages
    if (!context.packageManager.canRequestPackageInstalls()) {
        // Redirect user to settings to grant permission
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
            data = "package:${context.packageName}".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        Toast.makeText(
            context,
            "Please allow installing from this source",
            Toast.LENGTH_LONG
        ).show()
        return
    }

    // Use ACTION_VIEW for APK installation (not deprecated)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/vnd.android.package-archive")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(intent)
}
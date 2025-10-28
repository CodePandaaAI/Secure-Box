package com.romit.securebox.data.model

data class FileItem(
    val path: String,              // Full file path: "/storage/emulated/0/Download/photo.jpg"
    val name: String,              // File name: "photo.jpg"
    val isDirectory: Boolean,      // true if folder, false if file
    val size: String,                // Size in bytes: 1024000 (1MB)
    val lastModified: Long,        // Timestamp: 1698765432000
    val mimeType: String? = null,  // MIME type: "image/jpeg", null for folders
    val extension: String? = null  // Extension: "jpg", null for folders
)
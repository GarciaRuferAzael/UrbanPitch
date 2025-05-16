package com.example.urbanpitch.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.IOException

fun copyUriToTempFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
        tempFile
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

package com.example.peerprep.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


object ShareUtil {

    suspend fun shareImage(context: Context, imageUrl: String, fileName: String, useCache: Boolean) {
        val imageUri = if (useCache) {
            downloadImageToLocalCache(context, imageUrl, fileName)
        } else {
            downloadImageToExternalPath(context, imageUrl, fileName)
        }

        if (imageUri != null) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Image Via"))
        } else {
            Log.e("ShareUtil", "Image URI is null. Unable to share.")
        }
    }

    private suspend fun downloadImageToLocalCache(context: Context, imageUrl: String, fileName: String): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                val inputStream: InputStream = connection.inputStream
                val file = File(context.cacheDir, "$fileName.jpg")
                val outputStream = FileOutputStream(file)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ShareUtil", "Failed to download image: ${e.message}")
                null
            }
        }
    }

    private suspend fun downloadImageToExternalPath(context: Context, imageUrl: String, fileName: String): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                val inputStream: InputStream = connection.inputStream
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$fileName.jpg")
                val outputStream = FileOutputStream(file)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ShareUtil", "Failed to download image to external path: ${e.message}")
                null
            }
        }
    }
}



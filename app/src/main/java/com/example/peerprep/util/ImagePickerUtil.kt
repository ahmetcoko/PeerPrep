package com.example.peerprep.util


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher

object ImagePickerUtil {

    fun openGallery(activityResultLauncher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(intent)
    }

    fun openCamera(activityResultLauncher: ActivityResultLauncher<Intent>, activity: Activity) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activityResultLauncher.launch(intent)
    }

    fun handleImageResult(data: Intent?): Uri? {
        return data?.data
    }
}

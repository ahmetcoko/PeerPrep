package com.example.peerprep.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter



@Composable
fun LoadImage(imagePath: String?, contentDescription: String) {
    if (imagePath != null) {
        val painter = rememberAsyncImagePainter(model = imagePath)
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}


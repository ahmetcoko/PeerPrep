package com.example.peerprep.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.peerprep.R


enum class Screen(val icon: Int) {
    Feed(R.drawable.chat),
    Archive(R.drawable.file),
    Upload(R.drawable.upload),
    Profile(R.drawable.user);

    @Composable
    fun iconPainter(): Painter {
        return painterResource(id = icon)
    }
}


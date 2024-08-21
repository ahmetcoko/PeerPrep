package com.example.peerprep.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.example.peerprep.presentation.feed.FeedScreen
import com.example.peerprep.presentation.profile.ProfileScreen
import com.example.peerprep.presentation.archive.ArchiveScreen
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.peerprep.presentation.navigation.Screen
import com.example.peerprep.presentation.uploadQuestion.UploadQuestionScreen


@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(Screen.Feed) }

    Scaffold(
        bottomBar = { BottomNavigationBar(selectedTab, onTabSelected = { selectedTab = it }) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {

            when (selectedTab) {
                Screen.Feed -> FeedScreen()
                Screen.Upload -> UploadQuestionScreen()
                Screen.Archive -> ArchiveScreen()
                Screen.Profile -> ProfileScreen()
            }
        }
    }
}


@Composable
fun BottomNavigationBar(selectedTab: Screen, onTabSelected: (Screen) -> Unit) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface
    ) {
        Screen.values().forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = screen.iconPainter(),
                        contentDescription = screen.name,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                },
                label = { Text(screen.name) },
                selected = selectedTab == screen,
                onClick = { onTabSelected(screen) },
                alwaysShowLabel = false
            )
        }
    }
}






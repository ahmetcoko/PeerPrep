package com.example.peerprep.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel = hiltViewModel()) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text(text ="Profile Screen", modifier = Modifier.padding(16.dp))
        Button(
            onClick = { profileViewModel.signOut()},
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Sign Out")
        }
    }
}


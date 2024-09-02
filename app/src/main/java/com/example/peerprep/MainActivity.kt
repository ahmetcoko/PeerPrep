package com.example.peerprep

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.rememberNavController
import com.example.peerprep.presentation.MainScreen
import com.example.peerprep.presentation.auth.SignInViewModel
import com.example.peerprep.ui.theme.PeerPrepTheme
import com.example.peerprep.presentation.navigation.NavigationManager
import dagger.hilt.android.AndroidEntryPoint
import com.example.peerprep.presentation.navigation.NavigationHost
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var navigationManager: NavigationManager
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        checkLoginState()

        setContent {
            PeerPrepTheme {
                val navController = rememberNavController()
                NavigationHost(navigationManager)
            }
        }
    }

    private fun checkLoginState() {
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (isLoggedIn) {
            navigationManager.navigateToFeed()
        } else {
            navigationManager.navigateToSignIn()
        }
    }
}










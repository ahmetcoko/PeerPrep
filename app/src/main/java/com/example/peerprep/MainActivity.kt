package com.example.peerprep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.livedata.observeAsState
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
    private val signInViewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isLoggedIn = signInViewModel.isLoggedIn.observeAsState(false)

            PeerPrepTheme {
                if (isLoggedIn.value) {
                    MainScreen()
                } else {
                    NavigationHost(navigationManager)
                }
            }
        }
    }
}











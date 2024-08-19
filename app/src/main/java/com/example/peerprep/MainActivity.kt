package com.example.peerprep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.peerprep.presentation.auth.ForgotPasswordScreen
import com.example.peerprep.presentation.auth.SignInScreen
import com.example.peerprep.presentation.auth.SignUpScreen
import com.example.peerprep.presentation.feed.FeedScreen
import com.example.peerprep.ui.theme.PeerPrepTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PeerPrepTheme {
                // State to manage navigation
                var currentScreen by remember { mutableStateOf("SignIn") }

                when (currentScreen) {
                    "SignIn" -> SignInScreen(
                        onNavigateToSignUp = { currentScreen = "SignUp" },
                        onNavigateToForgotPassword = { currentScreen = "ForgotPassword" },  // Navigation to Forgot Password
                        onSignInSuccess = { currentScreen = "Feed" }
                    )
                    "SignUp" -> SignUpScreen(
                        onNavigateToSignIn = { currentScreen = "SignIn" }
                    )
                    "Feed" -> FeedScreen()
                    "ForgotPassword" ->     ForgotPasswordScreen(
                        onNavigateBack = { currentScreen = "SignIn" }
                    )
                }
            }
        }
    }
}





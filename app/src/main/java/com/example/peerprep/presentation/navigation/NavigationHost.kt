package com.example.peerprep.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.peerprep.presentation.MainScreen
import com.example.peerprep.presentation.auth.ForgotPasswordScreen
import com.example.peerprep.presentation.auth.SignInScreen
import com.example.peerprep.presentation.auth.SignUpScreen
import com.example.peerprep.presentation.feed.FeedScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavigationHost(navigationManager: NavigationManager) {
    val screen = navigationManager.currentScreen

    when (screen) {
        "SignIn" -> SignInScreen(
            onNavigateToSignUp = { navigationManager.navigateToSignUp() },
            onNavigateToForgotPassword = { navigationManager.navigateToForgotPassword() },
            onSignInSuccess = { navigationManager.navigateToFeed() }
        )
        "SignUp" -> SignUpScreen(onNavigateToSignIn = { navigationManager.navigateToSignIn() })
        "Feed" -> MainScreen(
            isLoggedIn = FirebaseAuth.getInstance().currentUser != null,
            navigationManager = navigationManager
        )
        "ForgotPassword" -> ForgotPasswordScreen(
            onNavigateBack = { navigationManager.navigateToSignIn() }
        )
    }
}


package com.example.peerprep.util

import androidx.compose.runtime.Composable
import com.example.peerprep.presentation.auth.ForgotPasswordScreen
import com.example.peerprep.presentation.auth.SignInScreen
import com.example.peerprep.presentation.auth.SignUpScreen
import com.example.peerprep.presentation.feed.FeedScreen

@Composable
fun NavigationHost(navigationManager: NavigationManager) {
    when (navigationManager.currentScreen) {
        "SignIn" -> SignInScreen(
            onNavigateToSignUp = { navigationManager.navigateToSignUp() },
            onNavigateToForgotPassword = { navigationManager.navigateToForgotPassword() },
            onSignInSuccess = { navigationManager.navigateToFeed() }
        )
        "SignUp" -> SignUpScreen(
            onNavigateToSignIn = { navigationManager.navigateToSignIn() }
        )
        "Feed" -> FeedScreen()
        "ForgotPassword" -> ForgotPasswordScreen(
            onNavigateBack = { navigationManager.navigateToSignIn() }
        )
    }
}
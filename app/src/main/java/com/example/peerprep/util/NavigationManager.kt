package com.example.peerprep.util
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class NavigationManager {

    var currentScreen by mutableStateOf("SignIn")

    fun navigateToSignIn() = run { currentScreen = "SignIn" }
    fun navigateToSignUp() = run { currentScreen = "SignUp" }
    fun navigateToFeed() = run { currentScreen = "Feed" }
    fun navigateToForgotPassword() = run { currentScreen = "ForgotPassword" }
}


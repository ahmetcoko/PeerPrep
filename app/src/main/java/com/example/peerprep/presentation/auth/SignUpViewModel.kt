package com.example.peerprep.presentation.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {
    val state = mutableStateOf(SignUpState())

    fun onEmailChanged(email: String) {
        state.value = state.value.copy(email = email)
    }

    fun onPasswordChanged(password: String) {
        state.value = state.value.copy(password = password)
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        state.value = state.value.copy(confirmPassword = confirmPassword)
    }

    fun onSignUpClicked() {
        // Implement sign-up logic, perhaps calling a use case
    }
}

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)

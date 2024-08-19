package com.example.peerprep.presentation.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.peerprep.domain.usecase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    var state = mutableStateOf(SignUpState())
        private set

    val passwordVisibility = mutableStateOf(false)
    val confirmPasswordVisibility = mutableStateOf(false)

    fun onSignUpClicked(context: Context) {
        val currentState = state.value
        if (currentState.email.isNotBlank() && currentState.password.isNotBlank()) {
            signUpUseCase(currentState.email, currentState.password,currentState.username ,currentState.name) { success, message ->
                if (success) {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                } else {

                }
            }
        }
    }

    // Simplified property updates
    fun onNameChanged(name: String) {
        state.value = state.value.copy(name = name)
    }

    fun onUsernameChanged(username: String) {
        state.value = state.value.copy(username = username)
    }

    fun onEmailChanged(email: String) {
        state.value = state.value.copy(email = email)
    }

    fun onPasswordChanged(password: String) {
        state.value = state.value.copy(password = password)
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        state.value = state.value.copy(confirmPassword = confirmPassword)
    }
}


data class SignUpState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val name: String = "",
    val username: String = ""
) {
    val isEmailValid get() = email.contains("@")
    val isPasswordValid get() = password.length >= 6
    val doPasswordsMatch get() = password == confirmPassword
    val isNameValid get() = name.length <= 15
    val isUsernameValid get() = username.length <= 10
}


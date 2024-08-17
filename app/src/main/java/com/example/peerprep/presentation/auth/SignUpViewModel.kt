package com.example.peerprep.presentation.auth

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
        private set  // Ensures state is only modifiable internally within the ViewModel

    val passwordVisibility = mutableStateOf(false)
    val confirmPasswordVisibility = mutableStateOf(false)

    fun onSignUpClicked() {
        val currentState = state.value
        if (currentState.email.isNotBlank() && currentState.password.isNotBlank()) {
            signUpUseCase(currentState.email, currentState.password) { success, message ->
                // Handle the response, e.g., show a message or update UI
                // This could be updating a LiveData or another state holder to communicate with the UI
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
)

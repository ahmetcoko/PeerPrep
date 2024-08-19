package com.example.peerprep.presentation.auth

import androidx.lifecycle.ViewModel
import com.example.peerprep.data.repository.FirebaseUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val userRepository: FirebaseUserRepository
): ViewModel() {

    fun resetPassword(email: String, onComplete: (String) -> Unit) {
        userRepository.sendPasswordResetEmail(email) { success, message ->
            if (success) {
                onComplete("Reset link sent to your email")
            } else {
                onComplete("Failed to send reset link: $message")
            }
        }
    }
}

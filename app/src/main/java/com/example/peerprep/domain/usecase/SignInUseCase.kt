package com.example.peerprep.domain.usecase

import com.example.peerprep.data.repository.FirebaseUserRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val userRepository: FirebaseUserRepository) {
    operator fun invoke(email: String, password: String, onComplete: (Boolean, String) -> Unit) {
        userRepository.signInWithEmailPassword(email, password, onComplete)
    }
}

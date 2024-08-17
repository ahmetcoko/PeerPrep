package com.example.peerprep.domain.usecase


import com.example.peerprep.data.repository.FirebaseUserRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val userRepository: FirebaseUserRepository // Repository layer injected
) {
    operator fun invoke(email: String, password: String, callback: (Boolean, String) -> Unit) {
        userRepository.signUpWithEmailPassword(email, password, callback)
    }
}



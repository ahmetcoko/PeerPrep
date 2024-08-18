package com.example.peerprep.domain.usecase


import com.example.peerprep.data.repository.FirebaseUserRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val userRepository: FirebaseUserRepository
) {
    operator fun invoke(email: String, password: String, username: String, name: String, callback: (Boolean, String) -> Unit) {
        userRepository.signUpWithEmailPassword(email, password, username, name, callback)
    }
}





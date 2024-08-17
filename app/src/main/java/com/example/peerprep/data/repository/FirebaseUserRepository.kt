package com.example.peerprep.data.repository




import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserRepository @Inject constructor() {
    private val auth: FirebaseAuth = Firebase.auth

    fun signUpWithEmailPassword(email: String, password: String, onComplete: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, "Registration Successful")
                } else {
                    onComplete(false, task.exception?.message ?: "Registration Failed")
                }
            }
    }
}

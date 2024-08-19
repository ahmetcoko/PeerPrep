package com.example.peerprep.data.repository




import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserRepository @Inject constructor(
    private val firestore: FirebaseFirestore // Inject Firestore instance
) {
    private val auth: FirebaseAuth = Firebase.auth

    fun signUpWithEmailPassword(
        email: String,
        password: String,
        username: String,
        name: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = hashMapOf(
                        "email" to email,
                        "username" to username,
                        "name" to name
                    )
                    saveUserToFirestore(user) { success, message ->
                        if (success) {
                            onComplete(true, "User $username created successfully")
                        } else {
                            onComplete(false, "User $username created, but failed to save data: $message")
                        }
                    }
                } else {
                    onComplete(false, task.exception?.message ?: "Registration Failed")
                }
            }
    }

    private fun saveUserToFirestore(
        userData: HashMap<String, String>,
        onComplete: (Boolean, String) -> Unit
    ) {
        firestore.collection("Users")
            .add(userData) // Using .add() instead of .document().set()
            .addOnSuccessListener { documentReference ->
                onComplete(true, "User data saved successfully with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message ?: "Failed to save user data")
            }
    }

    fun signInWithEmailPassword(email: String, password: String, onComplete: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, "Sign-in successful")
                } else {
                    onComplete(false, task.exception?.message ?: "Sign-in failed")
                }
            }
    }

    fun sendPasswordResetEmail(email: String, onComplete: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, "Email sent.")
                } else {
                    onComplete(false, task.exception?.message ?: "Error sending email.")
                }
            }
    }



}



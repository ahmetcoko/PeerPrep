package com.example.peerprep.data.repository




import androidx.media3.common.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
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
                    val userData = hashMapOf(
                        "email" to "ahmetcoko@gmail.com",
                        "name" to "ahmet2",
                        "username" to "Ahmetcoko"
                    )

                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    saveUserToFirestore(userData, uid) { success, message ->
                        if (success) {
                            Log.d("FirebaseUserRepository", message)
                        } else {
                            Log.e("FirebaseUserRepository", message)
                        }
                    }
                } else {
                    onComplete(false, task.exception?.message ?: "Registration Failed")
                }
            }
    }

    private fun saveUserToFirestore(
        userData: HashMap<String, String>,
        uid: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        firestore.collection("Users")
            .document(uid) // Use the UID as the document ID
            .set(userData) // Use .set() instead of .add()
            .addOnSuccessListener {
                onComplete(true, "User data saved successfully with UID: $uid")
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

    fun signOut(onComplete: () -> Unit) {
        auth.signOut()
        onComplete()
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    suspend fun getCurrentUserDetails(): UserDetails? {
        val user = auth.currentUser ?: return null

        try {
            val userDocument = firestore.collection("Users").document(user.uid).get().await()
            return if (userDocument.exists()) {
                userDocument.toObject(UserDetails::class.java)
            } else {
                Log.e("FirebaseUserRepository", "User document with UID ${user.uid} does not exist.")
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseUserRepository", "Error fetching user details", e)
            return null
        }
    }


}

data class UserDetails(
    val email: String = "",
    val name: String = "",
    val username: String = ""
)

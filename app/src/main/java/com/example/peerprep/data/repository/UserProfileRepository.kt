package com.example.peerprep.data.repository

import android.net.Uri
import android.util.Log
import com.example.peerprep.domain.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun getUserProfile(): UserProfile? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            val userDocument = firestore.collection("Users").document(userId).get().await()
            if (userDocument.exists()) {
                userDocument.toObject(UserProfile::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("UserProfileRepository", "Error fetching user profile", e)
            null
        }
    }

    fun uploadProfilePicture(uri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/$userId.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun saveUserProfilePicture(downloadUrl: String, onComplete: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val updates = mapOf("profilePictureUrl" to downloadUrl)

        firestore.collection("Users").document(userId).update(updates)
            .addOnSuccessListener {
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.e("UserProfileRepository", "Failed to save profile picture URL", e)
            }
    }
}

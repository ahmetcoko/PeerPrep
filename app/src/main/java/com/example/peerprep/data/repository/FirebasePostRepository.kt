package com.example.peerprep.data.repository

import com.example.peerprep.domain.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePostRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    fun getPosts(): Flow<List<Post>> = flow {
        val postCollection = firestore.collection("QuestionPosts")
        val snapshot = postCollection.get().await()
        val posts = snapshot.documents.mapNotNull { it.toObject(Post::class.java) }
        emit(posts)
    }
}

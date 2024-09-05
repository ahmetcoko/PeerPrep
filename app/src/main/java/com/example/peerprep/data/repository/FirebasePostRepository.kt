package com.example.peerprep.data.repository


import com.example.peerprep.domain.model.Comment
import com.example.peerprep.domain.model.Lesson
import com.example.peerprep.domain.model.Like
import com.example.peerprep.domain.model.Post
import com.example.peerprep.domain.model.Subtopic
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
        val posts = snapshot.documents.mapNotNull { document ->
            val lessonData = document.get("lessons") as? Map<String, Any>
            val lessons = lessonData?.let {
                Lesson(
                    name = it["name"] as? String ?: "",
                    subtopics = (it["subtopics"] as? List<Map<String, Any>>)?.map { subtopicData ->
                        Subtopic(
                            name = subtopicData["name"] as? String ?: "",
                            description = subtopicData["description"] as? String ?: ""
                        )
                    } ?: emptyList()
                )
            } ?: Lesson()
            document.toObject(Post::class.java)?.copy(lessons = lessons)
        }
        emit(posts)
    }



    suspend fun likePost(postId: String, like: Like) {
        val postRef = firestore.collection("QuestionPosts").document(postId)
        postRef.update("likes", FieldValue.arrayUnion(like)).await()
    }

    suspend fun unlikePost(postId: String, like: Like) {
        val postRef = firestore.collection("QuestionPosts").document(postId)
        postRef.update("likes", FieldValue.arrayRemove(like)).await()
    }

    suspend fun addCommentToPost(postId: String, comment: Comment) {
        val postRef = firestore.collection("QuestionPosts").document(postId)
        postRef.update("comments", FieldValue.arrayUnion(comment)).await()
    }
}


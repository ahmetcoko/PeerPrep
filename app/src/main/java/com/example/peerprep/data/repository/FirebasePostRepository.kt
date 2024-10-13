package com.example.peerprep.data.repository


import com.example.peerprep.domain.model.Comment
import com.example.peerprep.domain.model.Lesson
import com.example.peerprep.domain.model.Like
import com.example.peerprep.domain.model.Post
import com.example.peerprep.domain.model.Subtopic
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.example.peerprep.data.local.dao.PostDao
import com.example.peerprep.data.local.entities.CommentEntity
import com.example.peerprep.data.local.entities.LessonEntity
import com.example.peerprep.data.local.entities.LikeEntity
import com.example.peerprep.data.local.entities.PostEntity
import com.example.peerprep.data.local.entities.SubtopicEntity

@Singleton
class FirebasePostRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val postDao: PostDao
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


    suspend fun cachePostsToRoom(posts: List<PostEntity>) {
        postDao.deleteAllPosts()
        postDao.insertPosts(posts)
    }


    fun getPostsFromRoom(): Flow<List<PostEntity>> {
        return postDao.getAllPosts()
    }


    suspend fun syncPosts() {
        getPosts().collect { posts ->
            val postEntities = posts.mapNotNull { post ->
                post.lessons.subtopics.map {
                    it?.let { it1 ->
                        SubtopicEntity(
                            it1.name,
                            it?.description
                        )
                    }
                }?.let { LessonEntity(post.lessons.name, it) }?.let {
                    PostEntity(
                        postId = post.postId,
                        comment = post.comment,
                        downloadUrl = post.downloadUrl,
                        answer = post.answer,
                        date = post.date,
                        userName = post.userName,
                        fullName = post.fullName,
                        userEmail = post.userEmail,
                        lessons = it,
                        likes = post.likes.map { LikeEntity(it.userId, it.username) },
                        comments = post.comments.map { CommentEntity(it.userName, it.commentText, it.imageUrl, it.solved) }
                    )
                }
            }
            cachePostsToRoom(postEntities)
        }
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


    fun getLikedPostsByUser(userId: String): Flow<List<Post>> = flow {
        try {
            val querySnapshot = firestore.collection("QuestionPosts")
                .get()
                .await()
            val likedPosts = querySnapshot.documents.mapNotNull { document ->
                val post = document.toObject(Post::class.java)
                post?.let {
                    if (it.likes.any { like -> like.userId == userId }) {
                        post
                    } else {
                        null
                    }
                }
            }
            emit(likedPosts)
        } catch (e: Exception) {
            emit(emptyList<Post>())
        }
    }


}


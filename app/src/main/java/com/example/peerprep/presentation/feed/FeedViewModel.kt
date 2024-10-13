package com.example.peerprep.presentation.feed


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peerprep.data.local.dao.PostDao
import com.example.peerprep.data.local.entities.CommentEntity
import com.example.peerprep.data.local.entities.PostEntity
import com.example.peerprep.data.mappers.toComment
import com.example.peerprep.data.mappers.toEntity
import com.example.peerprep.domain.model.Post
import com.example.peerprep.data.repository.FirebasePostRepository
import com.example.peerprep.data.repository.FirebaseUserRepository
import com.example.peerprep.domain.model.Comment
import com.example.peerprep.domain.model.Lesson
import com.example.peerprep.domain.model.Like
import com.example.peerprep.domain.usecase.GetPostsUseCase
import com.example.peerprep.util.ImagePickerUtil
import com.example.peerprep.util.ShareUtil
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val postRepository: FirebasePostRepository,
    private val userRepository: FirebaseUserRepository,
    private val getPostsUseCase: GetPostsUseCase,
    private val postDao: PostDao,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _posts = MutableStateFlow<List<PostEntity>>(emptyList())
    val posts: StateFlow<List<PostEntity>> get() = _posts

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> get() = _currentUserId

    private val _currentUserName = MutableStateFlow<String?>(null)
    val currentUserName: StateFlow<String?> get() = _currentUserName

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing

    private val _imagePath = MutableStateFlow<Uri?>(null)
    val imagePath: StateFlow<Uri?> get() = _imagePath

    init {
        loadPosts()
        loadCurrentUserDetails()
    }

    fun setImagePath(uri: Uri?) {
        if (_imagePath.value != uri) {
            _imagePath.value = null
            _imagePath.value = uri
        }
    }

    fun shareImage(context: Context, imageUrl: String, fileName: String, useCache: Boolean) {
        viewModelScope.launch {
            ShareUtil.shareImage(context, imageUrl, fileName, useCache)
        }
    }


    fun getCurrentUserId(): String? {
        return userRepository.getCurrentUserId()
    }


    fun loadPosts() {
        viewModelScope.launch {
            getPostsUseCase.syncPosts()
            postDao.getAllPosts().collect { postEntities ->
                val sortedPosts = postEntities.sortedByDescending { it.date }
                _posts.value = sortedPosts
            }
        }
    }

    private fun loadCurrentUserDetails() {
        viewModelScope.launch {
            _currentUserId.value = userRepository.getCurrentUserId()
            _currentUserName.value = userRepository.getCurrentUserName()
        }
    }

    fun toggleLike(post: Post, currentUserId: String, currentUserName: String) {
        viewModelScope.launch {
            val like = Like(currentUserId, currentUserName)
            if (post.likes.any { it.userId == currentUserId }) {
                postRepository.unlikePost(post.postId, like)
            } else {
                postRepository.likePost(post.postId, like)
            }
            loadPosts()
        }
    }

    fun getCommentsForPost(postId: String): MutableStateFlow<List<CommentEntity>> {
        val post = _posts.value.find { it.postId == postId }
        return MutableStateFlow(post?.comments ?: emptyList())
    }


    suspend fun uploadImageToStorage(postId: String): String? {
        val imageUri = _imagePath.value ?: return null
        val uniqueFileName = "${System.currentTimeMillis()}_${postId}.jpg"
        val storageRef = storage.reference.child("CommentImages/$uniqueFileName")

        return try {
            val uploadTask = storageRef.putFile(imageUri).await()
            if (uploadTask.task.isSuccessful) {
                val downloadUrl = storageRef.downloadUrl.await().toString()
                Log.d("UploadComment", "Image uploaded successfully: $downloadUrl")
                downloadUrl
            } else {
                Log.e("UploadComment", "Upload task failed")
                null
            }
        } catch (e: Exception) {
            Log.e("UploadComment", "Error uploading image to storage", e)
            null
        }
    }


    fun addCommentToPost(postId: String, commentText: String, imageUri: Uri?, solved: Boolean) {
        viewModelScope.launch {
            val imageUrl = imageUri?.let { uploadImageToStorage(postId) }

            val comment = Comment(
                userName = _currentUserName.value ?: "",
                commentText = commentText,
                imageUrl = imageUrl,
                solved = solved
            )


            postRepository.addCommentToPost(postId, comment)


            val commentEntity = comment.toEntity()

            _posts.value = _posts.value.map { postEntity ->
                if (postEntity.postId == postId) {
                    postEntity.copy(comments = postEntity.comments + commentEntity)
                } else {
                    postEntity
                }
            }

            _imagePath.value = null
        }
    }



}
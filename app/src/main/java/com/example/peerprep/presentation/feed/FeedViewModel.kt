package com.example.peerprep.presentation.feed


import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peerprep.domain.model.Post
import com.example.peerprep.data.repository.FirebasePostRepository
import com.example.peerprep.data.repository.FirebaseUserRepository
import com.example.peerprep.domain.model.Comment
import com.example.peerprep.domain.model.Like
import com.example.peerprep.util.ImagePickerUtil
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
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> get() = _posts

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
        _imagePath.value = uri
    }

    fun showImagePickerDialog(
        galleryLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
        cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>
    ) {
        //add camera intent here
        ImagePickerUtil.openGallery(galleryLauncher)
    }


    fun getCurrentUserId(): String? {
        return userRepository.getCurrentUserId()
    }

    suspend fun getCurrentUserName(): String? {
        return userRepository.getCurrentUserName()
    }

    fun loadPosts() {
        viewModelScope.launch {
            postRepository.getPosts().collect { posts ->
                val sortedPosts = posts.sortedByDescending { it.date }
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

    fun getCommentsForPost(postId: String): StateFlow<List<Comment>> {
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


    fun addCommentToPost(postId: String, commentText: String, imageUri: Uri?) {
        viewModelScope.launch {
            val imageUrl = imageUri?.let { uploadImageToStorage(postId) }
            val comment = Comment(userName = _currentUserName.value ?: "", commentText = commentText, imageUrl = imageUrl)
            postRepository.addCommentToPost(postId, comment)
            _posts.value = _posts.value.map { post ->
                if (post.postId == postId) {
                    post.copy(comments = post.comments + comment)
                } else {
                    post
                }
            }
            _imagePath.value = null
        }
    }
}


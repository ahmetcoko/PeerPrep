package com.example.peerprep.presentation.archive

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peerprep.data.repository.FirebasePostRepository
import com.example.peerprep.data.repository.FirebaseUserRepository
import com.example.peerprep.domain.model.Comment
import com.example.peerprep.domain.model.Like
import com.example.peerprep.domain.model.Post
import com.example.peerprep.domain.usecase.GetLikedPostsUseCase
import com.example.peerprep.util.ShareUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val postRepository: FirebasePostRepository,
    private val userRepository: FirebaseUserRepository,
    private val getLikedPostsUseCase: GetLikedPostsUseCase,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> get() = _currentUserId

    private val _currentUserName = MutableStateFlow<String?>(null)
    val currentUserName: StateFlow<String?> get() = _currentUserName

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing

    private val _imagePath = MutableStateFlow<Uri?>(null)
    val imagePath: StateFlow<Uri?> get() = _imagePath

    private val _likedPosts = MutableStateFlow<List<Post>>(emptyList())
    val likedPosts: StateFlow<List<Post>> get() = _likedPosts


    init {
        loadLikedPosts()
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


    fun loadLikedPosts() {
        viewModelScope.launch {
            getCurrentUserId()?.let {
                getLikedPostsUseCase.execute(it).collect { posts ->
                    Log.d("ArchiveViewModel", "Liked posts loaded: ${posts.size}")
                    _likedPosts.value = posts
                }
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
            val updatedPosts = _likedPosts.value.toMutableList()

            val index = updatedPosts.indexOfFirst { it.postId == post.postId }
            if (index != -1) {
                val updatedPost = if (post.likes.any { it.userId == currentUserId }) {
                    postRepository.unlikePost(post.postId, like)
                    post.copy(likes = post.likes.filter { it.userId != currentUserId })
                } else {
                    postRepository.likePost(post.postId, like)
                    post.copy(likes = post.likes + like)
                }
                updatedPosts[index] = updatedPost
                _likedPosts.value = updatedPosts
            }
        }
        loadLikedPosts()
    }


    fun getCommentsForPost(postId: String): StateFlow<List<Comment>> {
        val post = _likedPosts.value.find { it.postId == postId }
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
            val comment = Comment(userName = _currentUserName.value ?: "", commentText = commentText, imageUrl = imageUrl, solved = solved)
            postRepository.addCommentToPost(postId, comment)
            _likedPosts.value = _likedPosts.value.map { post ->
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
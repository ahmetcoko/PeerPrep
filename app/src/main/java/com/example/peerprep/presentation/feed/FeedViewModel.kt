package com.example.peerprep.presentation.feed


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peerprep.domain.model.Post
import com.example.peerprep.data.repository.FirebasePostRepository
import com.example.peerprep.data.repository.FirebaseUserRepository
import com.example.peerprep.domain.model.Comment
import com.example.peerprep.domain.model.Like
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val postRepository: FirebasePostRepository,
    private val userRepository: FirebaseUserRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> get() = _posts

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> get() = _currentUserId

    private val _currentUserName = MutableStateFlow<String?>(null)
    val currentUserName: StateFlow<String?> get() = _currentUserName

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing

    init {
        loadPosts()
        loadCurrentUserDetails()
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


    fun addCommentToPost(postId: String, comment: Comment) {
        viewModelScope.launch {
            postRepository.addCommentToPost(postId, comment)
            _posts.value = _posts.value.map { post ->
                if (post.postId == postId) {
                    post.copy(comments = post.comments + comment)
                } else {
                    post
                }
            }
        }
    }

}

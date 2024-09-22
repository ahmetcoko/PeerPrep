package com.example.peerprep.domain.usecase



import com.example.peerprep.data.repository.FirebasePostRepository
import com.example.peerprep.domain.model.Post
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLikedPostsUseCase @Inject constructor(
    private val postRepository: FirebasePostRepository
) {
    fun execute(userId: String): Flow<List<Post>> {
        return postRepository.getLikedPostsByUser(userId)
    }
}

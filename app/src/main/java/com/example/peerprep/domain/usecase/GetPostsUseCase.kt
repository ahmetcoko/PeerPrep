package com.example.peerprep.domain.usecase

import com.example.peerprep.data.local.entities.PostEntity
import com.example.peerprep.data.repository.FirebasePostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val postRepository: FirebasePostRepository
) {

    fun execute(): Flow<List<PostEntity>> {
        return postRepository.getPostsFromRoom()
    }

    suspend fun syncPosts() {
        postRepository.syncPosts()
    }
}

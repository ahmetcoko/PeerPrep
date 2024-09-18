package com.example.peerprep.domain.usecase

import com.example.peerprep.data.repository.UserProfileRepository
import com.example.peerprep.domain.model.UserProfile
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend fun execute(): UserProfile? {
        return userProfileRepository.getUserProfile()
    }
}

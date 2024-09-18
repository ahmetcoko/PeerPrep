package com.example.peerprep.presentation.profile

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peerprep.data.repository.FirebaseUserRepository
import com.example.peerprep.data.repository.UserProfileRepository
import com.example.peerprep.domain.model.UserProfile
import com.example.peerprep.domain.usecase.GetUserProfileUseCase
import com.example.peerprep.presentation.navigation.NavigationManager
import com.example.peerprep.util.ImagePickerUtil
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseUserRepository: FirebaseUserRepository,
    private val navigationManager: NavigationManager,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val userProfileRepository: UserProfileRepository,
    application: Application
) : ViewModel() {

    private val sharedPreferences = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var userProfile by mutableStateOf<UserProfile?>(null)
        private set

    var profilePictureUri by mutableStateOf<Uri?>(null)

    init {
        viewModelScope.launch {
            userProfile = getUserProfileUseCase.execute()
        }
    }

    fun signOut() {
        firebaseUserRepository.signOut {
            navigationManager.navigateToSignIn()
            clearLoginState()
        }
    }

    private fun clearLoginState() {
        sharedPreferences.edit().remove("is_logged_in").apply()
    }


    fun uploadProfilePicture(uri: Uri) {
        profilePictureUri = uri
        userProfileRepository.uploadProfilePicture(uri,
            onSuccess = { downloadUrl ->
                updateProfilePictureUrl(downloadUrl)
            },
            onFailure = { exception ->
                Log.e("ProfileViewModel", "Failed to upload profile picture", exception)
            })
    }

    private fun updateProfilePictureUrl(downloadUrl: String) {
        val currentUser = userProfile ?: return
        val updatedProfile = currentUser.copy(profilePictureUrl = downloadUrl)

        userProfileRepository.saveUserProfilePicture(downloadUrl) {
            userProfile = updatedProfile
        }
    }
}



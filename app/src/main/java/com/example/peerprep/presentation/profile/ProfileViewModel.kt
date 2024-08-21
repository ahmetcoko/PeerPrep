package com.example.peerprep.presentation.profile

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.peerprep.data.repository.FirebaseUserRepository
import com.example.peerprep.presentation.navigation.NavigationManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseUserRepository: FirebaseUserRepository,
    private val navigationManager: NavigationManager,
    application: Application
) : ViewModel() {

    private val sharedPreferences = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun signOut() {
        firebaseUserRepository.signOut {
            navigationManager.navigateToSignIn()
            clearLoginState()
        }
    }

    private fun clearLoginState() {
        sharedPreferences.edit().remove("is_logged_in").apply()
    }
}



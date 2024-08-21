package com.example.peerprep.presentation.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.peerprep.domain.usecase.SignInUseCase
import com.example.peerprep.presentation.navigation.NavigationManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val navigationManager: NavigationManager,
    application: Application
) : AndroidViewModel(application) {

    private val _signInStatus = MutableLiveData<String>()
    val signInStatus: LiveData<String> get() = _signInStatus

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    private val sharedPreferences = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    init {
        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            _isLoggedIn.postValue(firebaseAuth.currentUser != null)
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _signInStatus.value = "Error: Email and password must not be empty"
            return
        }

        signInUseCase(email, password) { success, message ->
            _signInStatus.value = if (success) "Success" else "Error: $message"
            _isLoggedIn.value = success
            if (success) {
                navigationManager.navigateToFeed()
                setLoginState(true)
            }
        }
    }

    private fun setLoginState(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("is_logged_in", isLoggedIn).apply()
    }
}



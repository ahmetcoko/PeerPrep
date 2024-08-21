package com.example.peerprep.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.peerprep.domain.usecase.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val signInUseCase: SignInUseCase) : ViewModel() {

    private val _signInStatus = MutableLiveData<String>()
    val signInStatus: LiveData<String> get() = _signInStatus

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    fun signIn(email: String, password: String) {
        signInUseCase(email, password) { success, message ->
            _signInStatus.value = if (success) "Success" else "Error: $message"
            _isLoggedIn.value = success
        }
    }
}


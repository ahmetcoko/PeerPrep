package com.example.peerprep.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.peerprep.domain.usecase.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val signInUseCase: SignInUseCase) : ViewModel() {
    // LiveData to observe sign-in status
    private val _signInStatus = MutableLiveData<String>()
    val signInStatus: LiveData<String> get() = _signInStatus

    fun signIn(email: String, password: String) {
        signInUseCase(email, password) { success, message ->
            _signInStatus.value = if (success) "Success" else "Error: $message"
        }
    }
}

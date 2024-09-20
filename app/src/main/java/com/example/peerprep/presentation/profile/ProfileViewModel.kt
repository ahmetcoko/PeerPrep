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
import com.example.peerprep.domain.model.Department
import com.example.peerprep.domain.model.University
import com.example.peerprep.domain.model.UserProfile
import com.example.peerprep.domain.repository.UniversityRepository
import com.example.peerprep.domain.usecase.GetUserProfileUseCase
import com.example.peerprep.presentation.navigation.NavigationManager
import com.example.peerprep.util.ImagePickerUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseUserRepository: FirebaseUserRepository,
    private val navigationManager: NavigationManager,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val userProfileRepository: UserProfileRepository,
    private val universityRepository: UniversityRepository,
    private val firestore: FirebaseFirestore,
    application: Application
) : ViewModel() {

    private val sharedPreferences = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var userProfile by mutableStateOf<UserProfile?>(null)
        private set

    var profilePictureUri by mutableStateOf<Uri?>(null)

    private val _universities = MutableLiveData<List<University>>()
    val universities: LiveData<List<University>> get() = _universities

    private val _selectedUniversity = MutableLiveData<University?>()
    val selectedUniversity: LiveData<University?> get() = _selectedUniversity

    private val _departments = MutableLiveData<List<Department>>()
    val departments: LiveData<List<Department>> get() = _departments

    private val _selectedDepartment = MutableLiveData<Department?>()
    val selectedDepartment: LiveData<Department?> get() = _selectedDepartment

    private val _isChoosingTarget = MutableLiveData(false)
    val isChoosingTarget: LiveData<Boolean> get() = _isChoosingTarget

    init {
        viewModelScope.launch {
            userProfile = getUserProfileUseCase.execute()
            loadUniversities()
            loadTargetFromDatabase()
        }
    }

    private fun loadUniversities() {
        viewModelScope.launch {
            _universities.value = universityRepository.getUniversities()
        }
    }

    private fun loadTargetFromDatabase() {
        val userId = firebaseUserRepository.getCurrentUserId() ?: return

        firestore.collection("Users").document(userId).get()
            .addOnSuccessListener { document ->
                document?.let {
                    val universityName = it.getString("targetUniversity") ?: ""
                    val departmentName = it.getString("targetDepartment") ?: ""

                    if (universityName.isNotEmpty() && departmentName.isNotEmpty()) {
                        _selectedUniversity.value = _universities.value?.find { uni -> uni.name == universityName }
                        _selectedDepartment.value = _selectedUniversity.value?.departments?.find { dept -> dept.name == departmentName }
                        _isChoosingTarget.value = false
                    } else {
                        _isChoosingTarget.value = true
                    }
                }
            }
    }

    fun saveTargetSelection() {
        val userId = firebaseUserRepository.getCurrentUserId() ?: return
        val selectedUni = _selectedUniversity.value ?: return
        val selectedDept = _selectedDepartment.value ?: return

        val targetData = hashMapOf(
            "targetUniversity" to selectedUni.name,
            "targetDepartment" to selectedDept.name
        )

        firestore.collection("Users").document(userId).set(targetData, SetOptions.merge())
            .addOnSuccessListener {
                _isChoosingTarget.value = false
            }
            .addOnFailureListener {
                Log.e("ProfileViewModel", "Failed to save target data")
            }
    }

    fun selectUniversity(university: University) {
        _selectedUniversity.value = university
        _departments.value = university.departments
        _selectedDepartment.value = null
    }

    fun selectDepartment(department: Department) {
        _selectedDepartment.value = department
    }

    fun toggleChoosingTarget() {
        _isChoosingTarget.value = !_isChoosingTarget.value!!
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




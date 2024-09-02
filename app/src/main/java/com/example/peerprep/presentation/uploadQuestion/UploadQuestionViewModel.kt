package com.example.peerprep.presentation.uploadQuestion

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.peerprep.data.repository.FirebaseUserRepository
import com.example.peerprep.domain.model.Lesson
import com.example.peerprep.domain.model.Post
import com.example.peerprep.domain.model.Subtopic
import com.example.peerprep.domain.usecase.GetLessonsUseCase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class UploadQuestionViewModel @Inject constructor(
    private val getLessonsUseCase: GetLessonsUseCase,
    private val firebaseUserRepository: FirebaseUserRepository,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _lessons = MutableStateFlow<List<Lesson>>(emptyList())
    val lessons: StateFlow<List<Lesson>> get() = _lessons

    private val _selectedLesson = MutableStateFlow<Lesson?>(null)
    val selectedLesson: StateFlow<Lesson?> get() = _selectedLesson

    private val _selectedSubtopic = MutableStateFlow<Subtopic?>(null)
    val selectedSubtopic: StateFlow<Subtopic?> get() = _selectedSubtopic

    private val _imagePath = MutableStateFlow<Uri?>(null)
    val imagePath: StateFlow<Uri?> get() = _imagePath

    private val _userComment = MutableStateFlow("")
    val userComment: StateFlow<String> get() = _userComment

    private val _selectedChoice = MutableStateFlow<String?>(null)
    val selectedChoice: StateFlow<String?> get() = _selectedChoice

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> get() = _isUploading

    init {
        loadLessons()
    }

    private fun loadLessons() {
        viewModelScope.launch {
            _lessons.value = getLessonsUseCase()
        }
    }

    fun onLessonSelected(lesson: Lesson) {
        _selectedLesson.value = lesson
        _selectedSubtopic.value = null
    }

    fun onSubtopicSelected(subtopic: Subtopic) {
        _selectedSubtopic.value = subtopic
    }

    fun setImagePath(uri: Uri?) {
        _imagePath.value = uri
    }

    fun setUserComment(comment: String) {
        _userComment.value = comment
    }

    fun setSelectedChoice(choice: String) {
        _selectedChoice.value = choice
    }

    fun uploadQuestionPost(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isUploading.value = true

            try {
                val user = firebaseUserRepository.getCurrentUserDetails()
                if (user == null) {
                    Log.d("UploadQuestion", "User is null")
                    _isUploading.value = false
                    return@launch
                }

                val postId = firestore.collection("QuestionPosts").document().id
                val imageUrl = uploadImageToStorage(postId)

                val selectedSubtopicList = selectedSubtopic.value?.let { listOf(it) } ?: emptyList()

                val post = Post(
                    comment = userComment.value,
                    downloadUrl = imageUrl,
                    answer = selectedChoice.value ?: "",
                    date = Date(),
                    userName = user.username ?: "",
                    fullName = user.name ?: "",
                    userEmail = user.email ?: "",
                    lessons = Lesson(
                        name = selectedLesson.value?.name ?: "",
                        subtopics = selectedSubtopicList
                    ),
                    postId = postId
                )

                firestore.collection("QuestionPosts").document(postId)
                    .set(post)
                    .addOnCompleteListener {
                        _isUploading.value = false
                        if (it.isSuccessful) {
                            Log.d("UploadQuestion", "Question post uploaded successfully")
                            onSuccess()
                        } else {
                            Log.e("UploadQuestion", "Error uploading question post", it.exception)
                        }
                    }
            } catch (e: Exception) {
                Log.e("UploadQuestion", "Error in upload process", e)
                _isUploading.value = false
            }
        }
    }

    private suspend fun uploadImageToStorage(postId: String): String? {
        val imageUri = _imagePath.value ?: return null  // Return null immediately if no image path
        val storageRef = storage.reference.child("QuestionImages/$postId.jpg")

        return try {
            val uploadTask = storageRef.putFile(imageUri).await()
            if (uploadTask.task.isSuccessful) {
                val downloadUrl = storageRef.downloadUrl.await().toString()
                Log.d("UploadQuestion", "Image uploaded successfully: $downloadUrl")
                downloadUrl
            } else {
                Log.e("UploadQuestion", "Upload task failed")
                null
            }
        } catch (e: Exception) {
            Log.e("UploadQuestion", "Error uploading image to storage", e)
            null
        }
    }



}






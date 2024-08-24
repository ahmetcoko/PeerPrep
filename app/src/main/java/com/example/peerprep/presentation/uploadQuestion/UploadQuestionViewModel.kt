package com.example.peerprep.presentation.uploadQuestion

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peerprep.domain.model.Lesson
import com.example.peerprep.domain.model.Subtopic
import com.example.peerprep.domain.usecase.GetLessonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadQuestionViewModel @Inject constructor(
    private val getLessonsUseCase: GetLessonsUseCase
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
    val userComment: StateFlow<String> = _userComment

    private val _selectedChoice = MutableStateFlow<String?>(null)
    val selectedChoice: StateFlow<String?> = _selectedChoice

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
}


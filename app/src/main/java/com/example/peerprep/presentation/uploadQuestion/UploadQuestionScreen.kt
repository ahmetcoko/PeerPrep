package com.example.peerprep.presentation.uploadQuestion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.peerprep.domain.model.Lesson
import com.example.peerprep.domain.model.Subtopic
import com.example.peerprep.ui.theme.outline

@Composable
fun UploadQuestionScreen(
    viewModel: UploadQuestionViewModel = hiltViewModel()
) {
    val lessons by viewModel.lessons.collectAsState()
    val selectedLesson by viewModel.selectedLesson.collectAsState()
    val selectedSubtopic by viewModel.selectedSubtopic.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LessonDropdown(
            lessons = lessons,
            selectedLesson = selectedLesson,
            onLessonSelected = { viewModel.onLessonSelected(it) }
        )

        if (selectedLesson != null) {
            SubtopicDropdown(
                subtopics = selectedLesson!!.subtopics,
                selectedSubtopic = selectedSubtopic,
                onSubtopicSelected = { viewModel.onSubtopicSelected(it) }
            )
        }
    }
}

@Composable
fun LessonDropdown(
    lessons: List<Lesson>,
    selectedLesson: Lesson?,
    onLessonSelected: (Lesson) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = outline // Set the inside color of the button to light gray
            )) {
            Text(text = selectedLesson?.name ?: "Select Lesson")
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            lessons.forEach { lesson ->
                DropdownMenuItem(
                    text = { Text(lesson.name) },
                    onClick = {
                        onLessonSelected(lesson)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SubtopicDropdown(
    subtopics: List<Subtopic>,
    selectedSubtopic: Subtopic?,
    onSubtopicSelected: (Subtopic) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = outline // Set the inside color of the button to light gray
            )) {
            Text(text = selectedSubtopic?.name ?: "Select Subtopic")
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            subtopics.forEach { subtopic ->
                DropdownMenuItem(
                    text = { Text(subtopic.name) },
                    onClick = {
                        onSubtopicSelected(subtopic)
                        expanded = false
                    }
                )
            }
        }
    }
}
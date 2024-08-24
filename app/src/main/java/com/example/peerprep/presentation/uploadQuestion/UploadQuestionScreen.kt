package com.example.peerprep.presentation.uploadQuestion

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import java.util.Locale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.peerprep.R
import com.example.peerprep.domain.model.Lesson
import com.example.peerprep.domain.model.Subtopic
import com.example.peerprep.ui.theme.outline
import com.example.peerprep.util.ImagePickerUtil
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun UploadQuestionScreen(
    viewModel: UploadQuestionViewModel = hiltViewModel()
) {
    val lessons by viewModel.lessons.collectAsState()
    val selectedLesson by viewModel.selectedLesson.collectAsState()
    val selectedSubtopic by viewModel.selectedSubtopic.collectAsState()
    val imagePath by viewModel.imagePath.collectAsState()
    val userComment by viewModel.userComment.collectAsState()
    val selectedChoice by viewModel.selectedChoice.collectAsState()

    val activity = LocalContext.current as Activity
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = ImagePickerUtil.handleImageResult(result.data)
            viewModel.setImagePath(uri)
        } else {
            Toast.makeText(activity, "Image selection failed", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                viewModel.setImagePath(uri)
            }
        } else {
            Toast.makeText(activity, "Image capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Add space at the top
        Spacer(modifier = Modifier.height(32.dp)) // Adjust the height as needed

        ImageButton(imagePath = imagePath, onClick = {
            ImagePickerUtil.openGallery(galleryLauncher)
        })

        Spacer(modifier = Modifier.height(16.dp))



        OutlinedButton(
            onClick = {
                currentPhotoUri = ImagePickerUtil.openCamera(cameraLauncher, activity)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = outline // Set the inside color of the button to light gray
            )
        ) {
            Text("Open Camera")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TextField for user to enter a comment about the question
        TextField(
            value = userComment,
            onValueChange = { viewModel.setUserComment(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp)),
            placeholder = { Text("Enter comment of the question") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Multiple choice buttons
        MultipleChoiceRow(
            selectedChoice = selectedChoice,
            onChoiceSelected = { viewModel.setSelectedChoice(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

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
fun MultipleChoiceRow(
    selectedChoice: String?,
    onChoiceSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("A", "B", "C", "D", "E").forEach { choice ->
            OutlinedButton(
                onClick = { onChoiceSelected(choice) },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedChoice == choice) outline else Color.White
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(choice)
            }
        }
    }
}



@Composable
fun ImageButton(imagePath: Uri?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (imagePath != null) {
            val inputStream: InputStream? = LocalContext.current.contentResolver.openInputStream(imagePath)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Selected Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.upload_photo),
                contentDescription = "Upload Photo",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentScale = ContentScale.Fit
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
                containerColor = outline
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
                containerColor = outline
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
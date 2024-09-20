package com.example.peerprep.presentation.profile

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.TextField
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.peerprep.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val userProfile = profileViewModel.userProfile
    val profilePictureUri = profileViewModel.profilePictureUri

    val universities by profileViewModel.universities.observeAsState(emptyList())
    val departments by profileViewModel.departments.observeAsState(emptyList())
    val selectedUniversity by profileViewModel.selectedUniversity.observeAsState()
    val selectedDepartment by profileViewModel.selectedDepartment.observeAsState()
    val isChoosingTarget by profileViewModel.isChoosingTarget.observeAsState(false)

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                profileViewModel.uploadProfilePicture(it)
            }
        }
    }

    var universityExpanded by remember { mutableStateOf(false) }
    var departmentExpanded by remember { mutableStateOf(false) }

    val selectedUniversityName = selectedUniversity?.name ?: ""
    val selectedDepartmentName = selectedDepartment?.name ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Profile",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                },
                actions = {
                    if (!isChoosingTarget && selectedUniversity != null && selectedDepartment != null) {
                        IconButton(onClick = { profileViewModel.toggleChoosingTarget() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Target")
                        }
                    }
                    IconButton(onClick = { profileViewModel.signOut() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
                    }
                }
            )
        },
        floatingActionButton = {
            if (isChoosingTarget && selectedUniversity != null && selectedDepartment != null) {
                FloatingActionButton(
                    onClick = {
                        profileViewModel.saveTargetSelection()
                        profileViewModel.toggleChoosingTarget()
                    },
                ) {
                    androidx.compose.material.Icon(
                        painter = painterResource(id = R.drawable.save),
                        contentDescription = "Save Icon",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                            .clickable {
                                galleryLauncher.launch(
                                    Intent(
                                        Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        profilePictureUri?.let { uri ->
                            Image(
                                painter = rememberImagePainter(uri),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } ?: userProfile?.profilePictureUrl?.let { url ->
                            Image(
                                painter = rememberImagePainter(url),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("Name: ")
                                }
                                append(userProfile?.name ?: "")
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("Username: ")
                                }
                                append(userProfile?.username ?: "")
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("Email: ")
                                }
                                append(userProfile?.email ?: "")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isChoosingTarget && selectedUniversity != null && selectedDepartment != null) {
                    Column {
                        Text(
                            text = "$selectedUniversityName",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text(
                            text = "$selectedDepartmentName",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "${selectedDepartment!!.field}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Text(
                                text = "${selectedDepartment!!.rank}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Text(
                                text = "${selectedDepartment!!.score}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                } else if (!isChoosingTarget) {
                    Button(
                        onClick = { profileViewModel.toggleChoosingTarget() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("CHOOSE TARGET")
                    }
                } else {
                    ExposedDropdownMenuBox(
                        expanded = universityExpanded,
                        onExpandedChange = { universityExpanded = !universityExpanded }
                    ) {
                        TextField(
                            value = selectedUniversityName,
                            onValueChange = {},
                            label = { Text("Select University") },
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = universityExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = universityExpanded,
                            onDismissRequest = { universityExpanded = false }
                        ) {
                            universities.forEach { university ->
                                DropdownMenuItem(
                                    onClick = {
                                        profileViewModel.selectUniversity(university)
                                        universityExpanded = false
                                    }
                                ){
                                    Text(university.name)}
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    ExposedDropdownMenuBox(
                        expanded = departmentExpanded,
                        onExpandedChange = {
                            if (departments.isNotEmpty()) {
                                departmentExpanded = !departmentExpanded
                            }
                        }
                    ) {
                        TextField(
                            value = selectedDepartmentName,
                            onValueChange = {},
                            label = { Text("Select Department") },
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = departmentExpanded)
                            },
                            enabled = departments.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = departmentExpanded,
                            onDismissRequest = { departmentExpanded = false }
                        ) {
                            departments.forEach { department ->
                                DropdownMenuItem(
                                    onClick = {
                                        profileViewModel.selectDepartment(department)
                                        departmentExpanded = false
                                    }
                                ){
                                    Text(department.name)}
                            }
                        }
                    }
                }
            }
        }
    )
}













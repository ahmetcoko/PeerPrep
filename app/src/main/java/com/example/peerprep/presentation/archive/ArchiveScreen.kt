package com.example.peerprep.presentation.archive


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.peerprep.R
import com.example.peerprep.data.datasource.StaticLessonDataSource
import com.example.peerprep.domain.model.Post
import com.example.peerprep.presentation.feed.FeedViewModel
import com.example.peerprep.ui.theme.commentBackground
import com.example.peerprep.ui.theme.turquoise
import com.example.peerprep.util.ImagePickerUtil
import com.example.peerprep.util.ImageViewerDialog
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

@Composable
fun ArchiveScreen(viewModel: ArchiveViewModel = hiltViewModel()) {
    val posts by viewModel.filteredPosts.collectAsState()
    val lessons = StaticLessonDataSource.getLessons()
    val selectedLesson by viewModel.selectedLesson.collectAsState()
    val selectedSubtopic by viewModel.selectedSubtopic.collectAsState()

    val currentUserId by viewModel.currentUserId.collectAsState()
    val currentUserName by viewModel.currentUserName.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val currentPhotoUri by viewModel.imagePath.collectAsState()
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var showConfetti by remember { mutableStateOf(false) }

    var expandedLesson by remember { mutableStateOf(false) }
    var expandedSubtopic by remember { mutableStateOf(false) }

    val context = LocalContext.current as Activity

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = ImagePickerUtil.handleImageResult(result.data)
            viewModel.setImagePath(uri)
        } else {
            Toast.makeText(context, "Image selection failed", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri?.let { uri ->
                viewModel.setImagePath(uri)
            }
        } else {
            Toast.makeText(context, "Image capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadLikedPosts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {

            Box {
                IconButton(onClick = { expandedLesson = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.filter),
                        contentDescription = "Filter Icon",
                        modifier = Modifier.size(40.dp),
                        tint = Color.Unspecified
                    )
                }


                DropdownMenu(
                    expanded = expandedLesson,
                    onDismissRequest = { expandedLesson = false }
                ) {
                    lessons.forEach { lesson ->
                        DropdownMenuItem(onClick = {
                            viewModel.onLessonSelected(lesson)
                            expandedLesson = false
                        }) {
                            Text(text = lesson.name)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


            if (selectedLesson?.subtopics?.isNotEmpty() == true) {
                Box {
                    IconButton(onClick = { expandedSubtopic = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.filter),
                            contentDescription = "Filter Icon",
                            modifier = Modifier.size(40.dp),
                            tint = Color.Unspecified
                        )
                    }


                    DropdownMenu(
                        expanded = expandedSubtopic,
                        onDismissRequest = { expandedSubtopic = false }
                    ) {
                        selectedLesson?.subtopics?.forEach { subtopic ->
                            DropdownMenuItem(onClick = {
                                viewModel.onSubtopicSelected(subtopic)
                                expandedSubtopic = false
                            }) {
                                Text(text = subtopic.name)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


            if (selectedLesson != null || selectedSubtopic != null) {
                IconButton(onClick = {
                    viewModel.clearFilter()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.clear),
                        contentDescription = "Clear Filter",
                        modifier = Modifier.size(40.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }


        Box(modifier = Modifier.fillMaxSize()) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = {
                    viewModel.loadLikedPosts()
                }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(posts) { post: Post ->
                        if (currentUserId != null && currentUserName != null) {

                            PostItem(
                                post = post,
                                viewModel = viewModel,
                                currentUserId = currentUserId!!,
                                currentUserName = currentUserName!!,
                                galleryLauncher = galleryLauncher,
                                cameraLauncher = cameraLauncher,
                                currentPhotoUri = currentPhotoUri,
                                activity = context,
                                onOpenCamera = {
                                    photoUri = ImagePickerUtil.openCamera(cameraLauncher, context)
                                },
                                onSolved = { solved ->
                                    if (solved) {
                                        showConfetti = true
                                    }
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            if (showConfetti) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    SimpleCheckmarkAnimation()

                    LaunchedEffect(Unit) {
                        delay(3000)
                        showConfetti = false
                    }
                }
            }
        }//box kapanıyor
    }
}






@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostItem(
    post: Post,
    viewModel: ArchiveViewModel,
    currentUserId: String,
    currentUserName: String,
    galleryLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    currentPhotoUri: Uri?,
    activity: Activity,
    onOpenCamera: () -> Unit,
    onSolved: (Boolean) -> Unit
) {
    var isImageDialogVisible by remember { mutableStateOf(false) }
    var imageUrlForDialog by remember { mutableStateOf<String?>(null) }


    var isCommentsVisible by remember { mutableStateOf(false) }

    val comments by viewModel.getCommentsForPost(post.postId).collectAsState(initial = emptyList())

    var isLessonDetailsVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile Photo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Text(
                        text = post.userName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    if (isLessonDetailsVisible) {
                        post.lessons?.let { lesson ->
                            Column {
                                androidx.compose.material3.Text(
                                    text = lesson.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                if (lesson.subtopics.isNotEmpty()) {
                                    androidx.compose.material3.Text(
                                        text = lesson.subtopics.joinToString { it.name },
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            IconButton(onClick = { isLessonDetailsVisible = !isLessonDetailsVisible }) {
                Icon(
                    painter = painterResource(id = R.drawable.detail),
                    contentDescription = "Details Icon",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))


        post.downloadUrl?.let { url ->
            Image(
                painter = rememberAsyncImagePainter(model = url),
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            imageUrlForDialog = url
                            isImageDialogVisible = true
                        }
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {
                viewModel.toggleLike(post, currentUserId, currentUserName)
            }) {
                Icon(
                    imageVector = if (post.likes.any { it.userId == currentUserId }) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (post.likes.any { it.userId == currentUserId }) Color.Red else Color.Black
                )
            }

            androidx.compose.material3.Text(text = "${post.likes.size}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    isCommentsVisible = !isCommentsVisible
                }) {
                    Icon(imageVector = Icons.Default.Comment, contentDescription = "Comment", tint = Color.Black)
                }

                androidx.compose.material3.Text(
                    text = "${comments.size}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = {
                if (post.downloadUrl.isNullOrEmpty()) {
                    Toast.makeText(activity, "No image available for sharing", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.shareImage(activity, post.downloadUrl!!, "Shared Image", useCache = true)
                }
            }) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        BasicTextField(
            value = post.comment,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )


        if (isCommentsVisible) {
            Column(modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(commentBackground)) {
                comments.forEach { comment ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            if (comment.solved) {
                                Icon(
                                    painter = painterResource(id = R.drawable.tick),
                                    contentDescription = "Solved",
                                    tint = Color.Unspecified,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(end = 4.dp)
                                )
                            }


                            androidx.compose.material3.Text(
                                modifier = Modifier.padding(top = 4.dp),
                                text = "${comment.userName}",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )


                            androidx.compose.material3.Text(
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                                text = ": ${comment.commentText}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }


                        comment.imageUrl?.let { imageUrl ->
                            Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = "Comment Image",
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .combinedClickable(
                                        onClick = {},
                                        onLongClick = {
                                            imageUrlForDialog = imageUrl
                                            isImageDialogVisible = true
                                        }
                                    ),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                CommentInputField(
                    onCommentSubmitted = { commentText, photoUri, selectedAnswer ->

                        val correctAnswer = post.answer
                        val solved = selectedAnswer == correctAnswer

                        viewModel.addCommentToPost(post.postId, commentText, photoUri, solved)


                        onSolved(solved)
                    },
                    galleryLauncher = galleryLauncher,
                    cameraLauncher = cameraLauncher,
                    currentPhotoUri = currentPhotoUri,
                    viewModel = viewModel,
                    activity = activity,
                    onOpenCamera = onOpenCamera
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    imageUrlForDialog?.let { imageUrl ->
        if (isImageDialogVisible) {
            ImageViewerDialog(imageUrl = imageUrl, onDismiss = {
                isImageDialogVisible = false
                imageUrlForDialog = null
            })
        }
    }
}




@Composable
fun CommentInputField(
    onCommentSubmitted: (String, Uri?, String) -> Unit,
    galleryLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    currentPhotoUri: Uri?,
    viewModel: ArchiveViewModel,
    activity: Activity,
    onOpenCamera: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedAnswer by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = commentText,
            onValueChange = { commentText = it },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp)),
            placeholder = { Text("Enter comment") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            )
        )


        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.answer),
                    contentDescription = "Select Answer",
                    tint = Color.Unspecified
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("A", "B", "C", "D", "E").forEach { answer ->
                    DropdownMenuItem(onClick = {
                        selectedAnswer = answer
                        expanded = false
                    }) {
                        androidx.compose.material3.Text(answer)
                    }
                }
            }
        }

        IconButton(
            onClick = { ImagePickerUtil.openGallery(galleryLauncher) }
        ) {
            Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = "Pick from Gallery")
        }

        IconButton(
            onClick = { onOpenCamera() }
        ) {
            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Capture Photo")
        }

        IconButton(
            onClick = {
                if (commentText.isNotEmpty()) {
                    onCommentSubmitted(commentText, currentPhotoUri, selectedAnswer)
                    commentText = ""
                    selectedAnswer = ""
                    keyboardController?.hide()
                }
            }
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Submit Comment")
        }
    }

    currentPhotoUri?.let { uri ->
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = "Selected Image",
            modifier = Modifier
                .padding(8.dp)
                .height(100.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray)
        )
    }
}


@Composable
fun SimpleCheckmarkAnimation() {
    val transition = rememberInfiniteTransition(label = "")

    val strokeSize by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    LaunchedEffect(Unit) {
        delay(2000)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(200.dp)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2 - 10.dp.toPx()


            drawCircle(
                color = Color.Green,
                center = center,
                radius = radius,
                style = Stroke(width = 10.dp.toPx() * strokeSize)
            )


            val checkPath = Path().apply {
                moveTo(center.x - radius * 0.5f, center.y)
                lineTo(center.x - radius * 0.1f, center.y + radius * 0.4f)
                lineTo(center.x + radius * 0.5f, center.y - radius * 0.4f)
            }


            drawPath(
                path = checkPath,
                color = Color.Green,
                style = Stroke(width = 10.dp.toPx() * strokeSize, cap = StrokeCap.Round)
            )
        }
    }
}

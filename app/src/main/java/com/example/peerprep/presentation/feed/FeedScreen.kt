package com.example.peerprep.presentation.feed


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.peerprep.R
import com.example.peerprep.domain.model.Post
import coil.compose.rememberAsyncImagePainter
import com.example.peerprep.ui.theme.commentBackground
import com.example.peerprep.ui.theme.turquoise
import com.example.peerprep.util.ImagePickerUtil
import com.example.peerprep.util.ImageViewerDialog
import com.example.peerprep.util.ShareUtil
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@Composable
fun FeedScreen(viewModel: FeedViewModel = hiltViewModel()) {
    val posts by viewModel.posts.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    val currentUserName by viewModel.currentUserName.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val currentPhotoUri by viewModel.imagePath.collectAsState()
    var photoUri by remember { mutableStateOf<Uri?>(null) }

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
        viewModel.loadPosts()
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            viewModel.loadPosts()
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
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}






@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostItem(
    post: Post,
    viewModel: FeedViewModel,
    currentUserId: String,
    currentUserName: String,
    galleryLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    currentPhotoUri: Uri?,
    activity: Activity,
    onOpenCamera: () -> Unit
) {
    var isImageDialogVisible by remember { mutableStateOf(false) }
    var imageUrlForDialog by remember { mutableStateOf<String?>(null) }

    val isLiked = post.likes.any { it.userId == currentUserId }
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
                    Text(
                        text = post.userName,
                        fontWeight = FontWeight.Bold,
                        style = typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    if (isLessonDetailsVisible) {
                        post.lessons?.let { lesson ->
                            Column {
                                Text(
                                    text = lesson.name,
                                    style = typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                if (lesson.subtopics.isNotEmpty()) {
                                    Text(
                                        text = lesson.subtopics.joinToString { it.name },
                                        style = typography.bodyMedium
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
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) Color.Red else Color.Black
                )
            }

            Text(text = "${post.likes.size}", style = typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    isCommentsVisible = !isCommentsVisible
                }) {
                    Icon(imageVector = Icons.Default.Comment, contentDescription = "Comment", tint = Color.Black)
                }

                Text(
                    text = "${comments.size}",
                    style = typography.bodyLarge,
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
            Column(modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(commentBackground)) {
                comments.forEach { comment ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row {
                            Text(
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                                text = "${comment.userName}",
                                style = typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                modifier = Modifier.padding(end = 16.dp, top = 4.dp),
                                text = ": ${comment.commentText}",
                                style = typography.bodyLarge
                            )
                        }


                        comment.imageUrl?.let { imageUrl ->
                            Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = "Comment Image",
                                modifier = Modifier
                                    .padding(8.dp)
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
                    onCommentSubmitted = { commentText, photoUri ->
                        viewModel.addCommentToPost(post.postId, commentText, photoUri)
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
        HorizontalDivider(thickness = 2.dp, color = turquoise)
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
    onCommentSubmitted: (String, Uri?) -> Unit,
    galleryLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    currentPhotoUri: Uri?,
    viewModel: FeedViewModel,
    activity: Activity,
    onOpenCamera: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }

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
            placeholder = { androidx.compose.material.Text("Enter comment") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            )
        )


        IconButton(
            onClick = {
                ImagePickerUtil.openGallery(galleryLauncher)
            }
        ) {
            Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = "Pick from Gallery")
        }


        IconButton(
            onClick = {
                onOpenCamera()
            }
        ) {
            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Capture Photo")
        }

        IconButton(
            onClick = {
                if (commentText.isNotEmpty()) {
                    onCommentSubmitted(commentText, currentPhotoUri)
                    commentText = ""
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











package com.example.peerprep.presentation.feed

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
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.peerprep.R
import com.example.peerprep.domain.model.Post
import coil.compose.rememberAsyncImagePainter
import com.example.peerprep.domain.model.Comment
import com.example.peerprep.ui.theme.commentBackground
import com.example.peerprep.ui.theme.turquoise
import com.example.peerprep.util.ImageViewerDialog
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@Composable
fun FeedScreen(viewModel: FeedViewModel = hiltViewModel()) {
    val posts by viewModel.posts.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    val currentUserName by viewModel.currentUserName.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

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
                        currentUserName = currentUserName!!
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}




@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostItem(post: Post, viewModel: FeedViewModel, currentUserId: String, currentUserName: String) {
    var isImageDialogVisible by remember { mutableStateOf(false) }
    val isLiked = post.likes.any { it.userId == currentUserId }
    var isCommentsVisible by remember { mutableStateOf(false) }
    val comments by viewModel.getCommentsForPost(post.postId).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile Photo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = post.userName,
                fontWeight = FontWeight.Bold,
                style = typography.bodyLarge
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(id = R.drawable.detail),
                contentDescription = "Details Icon",
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
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
                        onLongClick = { isImageDialogVisible = true }
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isImageDialogVisible) {
                ImageViewerDialog(imageUrl = url) { isImageDialogVisible = false }
            }
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
            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.Black)
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
                    Row {

                        Text(modifier = Modifier.padding(start = 16.dp,top = 4.dp),text = "${comment.userName}", style = typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                        Text(modifier = Modifier.padding(end = 16.dp, top = 4.dp),text = ": ${comment.commentText}", style = typography.bodyLarge)
                    }
                }
                CommentInputField(
                    onCommentSubmitted = { commentText ->
                        viewModel.addCommentToPost(post.postId, Comment(currentUserName, commentText))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 2.dp, color = turquoise)
    }
}

@Composable
fun CommentInputField(onCommentSubmitted: (String) -> Unit) {
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
            placeholder = { androidx.compose.material.Text("Enter comment of the question") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray,
            )
        )
        IconButton(
            onClick = {
                if (commentText.isNotEmpty()) {
                    onCommentSubmitted(commentText)
                    commentText = ""
                }
            }
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Submit Comment")
        }
    }
}



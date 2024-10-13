package com.example.peerprep.data.local.entities

import androidx.room.Entity

@Entity(tableName = "comments")
data class CommentEntity(
    val userName: String,
    val commentText: String,
    val imageUrl: String?,
    val solved: Boolean
)



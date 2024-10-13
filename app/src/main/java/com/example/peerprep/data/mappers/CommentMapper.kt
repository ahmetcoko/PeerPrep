package com.example.peerprep.data.mappers

import com.example.peerprep.data.local.entities.CommentEntity
import com.example.peerprep.domain.model.Comment

fun Comment.toEntity(): CommentEntity {
    return CommentEntity(
        userName = this.userName,
        commentText = this.commentText,
        imageUrl = this.imageUrl,
        solved = this.solved
    )
}

fun CommentEntity.toComment(): Comment {
    return Comment(
        userName = this.userName,
        commentText = this.commentText,
        imageUrl = this.imageUrl,
        solved = this.solved
    )
}


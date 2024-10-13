package com.example.peerprep.data.mappers

import com.example.peerprep.data.local.entities.CommentEntity
import com.example.peerprep.data.local.entities.PostEntity
import com.example.peerprep.domain.model.Comment
import com.example.peerprep.domain.model.Lesson
import com.example.peerprep.domain.model.Like
import com.example.peerprep.domain.model.Post
import com.example.peerprep.domain.model.Subtopic


fun PostEntity.toPost(): Post {
    return Post(
        postId = this.postId,
        comment = this.comment,
        downloadUrl = this.downloadUrl,
        answer = this.answer,
        date = this.date,
        userName = this.userName,
        fullName = this.fullName,
        userEmail = this.userEmail,
        lessons = Lesson(
            name = this.lessons.name,
            subtopics = this.lessons.subtopics.map { it?.let { it1 -> it.description?.let { it2 ->
                Subtopic(it1.name ,
                    it2
                )
            } } }
        ),
        likes = this.likes.map { Like(it.userId, it.username) },
        comments = this.comments.map {
            val commentEntity = it as CommentEntity
            Comment(
                userName = commentEntity.userName,
                commentText = commentEntity.commentText,
                imageUrl = commentEntity.imageUrl,
                solved = commentEntity.solved
            )
        }
    )
}

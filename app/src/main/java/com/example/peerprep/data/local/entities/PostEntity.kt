package com.example.peerprep.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.peerprep.data.local.Converters
import java.util.Date

@Entity(tableName = "posts")
@TypeConverters(Converters::class)
data class PostEntity(
    @PrimaryKey val postId: String,
    val comment: String,
    val downloadUrl: String?,
    val answer: String,
    val date: Date,
    val userName: String,
    val fullName: String,
    val userEmail: String,
    val lessons: LessonEntity,
    val likes: List<LikeEntity>,
    val comments: List<CommentEntity>
)



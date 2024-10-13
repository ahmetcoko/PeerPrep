package com.example.peerprep.domain.model

import java.util.Date

data class Post(
    val comment: String = "",
    val downloadUrl: String? = null,
    val answer: String = "",
    val date: Date = Date(),
    val userName: String = "",
    val fullName: String = "",
    val userEmail: String = "",
    val lessons: Lesson = Lesson(),
    val postId: String = "",
    val likes: List<Like> = emptyList(),
    val comments: List<Comment> = emptyList()
) {
    constructor() : this("", "", "", Date(), "", "", "", Lesson(), "" , emptyList<Like>(), emptyList<Comment>())

}

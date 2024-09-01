package com.example.peerprep.domain.model

import java.util.Date

data class Post(
    val comment: String = "",
    val downloadUrl: String = "",
    val answer: String = "",
    val date: Date = Date(),
    val userName: String = "",
    val fullName: String = "",
    val userEmail: String = "",
    val lessons: Lesson = Lesson(),
    val postId: String = ""
) {
    constructor() : this("", "", "", Date(), "", "", "", Lesson(), "")
}

package com.example.peerprep.domain.model

data class Comment(
    val userName: String = "",
    val commentText: String = "",
    val imageUrl: String? = null,
    val solved: Boolean = false
) {
    constructor() : this("", "", null, false)
}

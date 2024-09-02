package com.example.peerprep.domain.model

data class Comment(
    val userName: String = "",
    val commentText: String = ""
){
    constructor() : this("", "")
}

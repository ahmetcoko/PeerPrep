package com.example.peerprep.domain.model

data class Like(
    val userId: String = "",
    val username: String = ""
) {
    constructor() : this("", "")
}


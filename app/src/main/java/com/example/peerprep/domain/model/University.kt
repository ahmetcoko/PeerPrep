package com.example.peerprep.domain.model

data class University(
    val name: String,
    var departments: List<Department>
)

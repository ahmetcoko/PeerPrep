package com.example.peerprep.domain.repository

import com.example.peerprep.domain.model.University


interface UniversityRepository {
    suspend fun getUniversities(): List<University>
}

package com.example.peerprep.domain.repository

import com.example.peerprep.domain.model.Lesson

interface LessonRepository {
    fun getLessons(): List<Lesson>
}
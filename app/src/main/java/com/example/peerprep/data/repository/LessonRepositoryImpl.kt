package com.example.peerprep.data.repository

import com.example.peerprep.data.datasource.StaticLessonDataSource
import com.example.peerprep.domain.model.Lesson
import com.example.peerprep.domain.repository.LessonRepository

class LessonRepositoryImpl : LessonRepository {

    override fun getLessons(): List<Lesson> {
        return StaticLessonDataSource.getLessons()
    }
}
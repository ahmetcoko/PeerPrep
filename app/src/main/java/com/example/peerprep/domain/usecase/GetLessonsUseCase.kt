package com.example.peerprep.domain.usecase

import com.example.peerprep.domain.model.Lesson
import com.example.peerprep.domain.repository.LessonRepository

class GetLessonsUseCase(private val lessonRepository: LessonRepository) {

    operator fun invoke(): List<Lesson> {
        return lessonRepository.getLessons()
    }
}
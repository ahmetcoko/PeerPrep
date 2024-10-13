package com.example.peerprep.data.local.entities

import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.peerprep.data.local.Converters

@Entity
data class LessonEntity(
    val name: String,
    @TypeConverters(Converters::class) val subtopics: List<SubtopicEntity?>
)



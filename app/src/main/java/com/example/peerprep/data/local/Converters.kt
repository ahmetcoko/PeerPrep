package com.example.peerprep.data.local

import androidx.room.TypeConverter
import com.example.peerprep.data.local.entities.CommentEntity
import com.example.peerprep.data.local.entities.LessonEntity
import com.example.peerprep.data.local.entities.LikeEntity
import com.example.peerprep.data.local.entities.SubtopicEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }

    @TypeConverter
    fun listToString(list: List<String>?): String? {
        return list?.joinToString(",")
    }

    private val gson = Gson()

    @TypeConverter
    fun fromLikeEntityList(value: List<LikeEntity>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toLikeEntityList(value: String): List<LikeEntity>? {
        val listType = object : TypeToken<List<LikeEntity>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromCommentEntityList(comments: List<CommentEntity>?): String {
        return Gson().toJson(comments)
    }

    @TypeConverter
    fun toCommentEntityList(commentsString: String): List<CommentEntity>? {
        val listType = object : TypeToken<List<CommentEntity>>() {}.type
        return Gson().fromJson(commentsString, listType)
    }

    @TypeConverter
    fun fromSubtopicEntityList(value: List<SubtopicEntity>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toSubtopicEntityList(value: String): List<SubtopicEntity>? {
        val listType = object : TypeToken<List<SubtopicEntity>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromLessonEntity(value: LessonEntity?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toLessonEntity(value: String?): LessonEntity? {
        return gson.fromJson(value, LessonEntity::class.java)
    }
}

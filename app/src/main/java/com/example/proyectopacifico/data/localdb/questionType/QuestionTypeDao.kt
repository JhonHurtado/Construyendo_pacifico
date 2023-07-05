package com.example.proyectopacifico.data.localdb.questionType

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.proyectopacifico.data.models.entities.arquitecture.QuestionTypeEntity

@Dao
interface QuestionTypeDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveQuestionType(questionTypeEntity: QuestionTypeEntity):Long
}
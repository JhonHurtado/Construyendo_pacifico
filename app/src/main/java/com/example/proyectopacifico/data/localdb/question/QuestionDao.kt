package com.example.proyectopacifico.data.localdb.question

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectopacifico.data.models.entities.arquitecture.questions.QuestionEntity

@Dao
interface QuestionDao {

    @Query("select * from questionentity where question_type_id = :question_type_id")
    suspend fun fetchAllQuestions(question_type_id : Int):List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveQuestion(questionEntity: QuestionEntity):Long

}
package com.example.proyectopacifico.data.localdb.answer

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswerAndQuestion
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswersEntity

@Dao
interface AnswerDao {

    @Query("SELECT *,a.question_id as question_answer_id FROM answersentity as a join questionentity as q on a.question_id = q.id_question  where place_id = :id_place")
    suspend fun fetchAnswersByPlaceId(id_place:Int):List<AnswerAndQuestion>

    @Query("SELECT * FROM AnswersEntity")
    suspend fun getAllAnswers(): List<AnswersEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveAnswer(answersEntity: AnswersEntity):Long

    @Delete
    suspend fun deleteAnswer(answersEntity: AnswersEntity):Int
}
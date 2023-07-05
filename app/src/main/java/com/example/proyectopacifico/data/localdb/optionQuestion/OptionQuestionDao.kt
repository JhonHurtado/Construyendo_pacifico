package com.example.proyectopacifico.data.localdb.optionQuestion

import androidx.room.*
import com.example.proyectopacifico.data.models.entities.arquitecture.OptionQuestionEntity
import com.example.proyectopacifico.data.models.entities.arquitecture.questions.OptionQuestionAndQuestion

@Dao
interface OptionQuestionDao {

    @Transaction
    @Query("select *,q.question_id as question_father_id from optionquestionentity as o  join questionentity as q on o.question_id = q.id_question where o.question_id = :question_id")
    suspend fun fetchOptionQuestionAndQuestionByQuestionId(question_id:Int):List<OptionQuestionAndQuestion>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveOptionQuestion(optionQuestionEntity: OptionQuestionEntity):Long
}
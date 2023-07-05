package com.example.proyectopacifico.domain.answer

import com.example.proyectopacifico.data.models.entities.arquitecture.AnswerAndQuestion
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswersEntity
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.answers.AnswerBody
import retrofit2.http.Body

interface AnswerRepo {
    //local db
    suspend fun fetchAnswersByPlaceId(id_place:Int):List<AnswerAndQuestion>
    suspend fun saveAnswer(answersEntity: AnswersEntity):Long
    suspend fun deleteAnswer(answersEntity: AnswersEntity):Int
    suspend fun getAllAnswers(): List<AnswersEntity>

    //web
    suspend fun saveWebAnswers(answerBody: AnswerBody): BaseObjectResponse<Any>
}
package com.example.proyectopacifico.domain.answer

import com.example.proyectopacifico.data.localdb.answer.AnswerDao
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswerAndQuestion
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswersEntity
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.answers.AnswerBody
import com.example.proyectopacifico.data.rest.WebService

class AnswerRepoImpl (private val dao : AnswerDao,private val rest : WebService): AnswerRepo {
    //local db
    override suspend fun fetchAnswersByPlaceId(id_place:Int):List<AnswerAndQuestion> = dao.fetchAnswersByPlaceId(id_place)
    override suspend fun saveAnswer(answersEntity: AnswersEntity): Long = dao.saveAnswer(answersEntity)
    override suspend fun deleteAnswer(answersEntity: AnswersEntity): Int = dao.deleteAnswer(answersEntity)
    override suspend fun getAllAnswers(): List<AnswersEntity> = dao.getAllAnswers()

    //web
    override suspend fun saveWebAnswers(answerBody: AnswerBody): BaseObjectResponse<Any> = rest.saveWebAnswers(answerBody)
}
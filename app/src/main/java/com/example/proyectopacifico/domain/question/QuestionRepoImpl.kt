package com.example.proyectopacifico.domain.question

import com.example.proyectopacifico.data.localdb.answer.AnswerDao
import com.example.proyectopacifico.data.localdb.question.QuestionDao
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswersEntity
import com.example.proyectopacifico.data.models.entities.arquitecture.questions.QuestionEntity

class QuestionRepoImpl(private val dao : QuestionDao):QuestionRepo {
    override suspend fun saveQuestion(questionEntity: QuestionEntity): Long = dao.saveQuestion(questionEntity)
    override suspend fun fetchAllQuestions(question_type_id : Int): List<QuestionEntity> = dao.fetchAllQuestions(question_type_id)
}
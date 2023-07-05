package com.example.proyectopacifico.domain.questionType

import com.example.proyectopacifico.data.localdb.questionType.QuestionTypeDao
import com.example.proyectopacifico.data.models.entities.arquitecture.QuestionTypeEntity

class QuestionTypeRepoImpl(private val dao: QuestionTypeDao) : QuestionTypeRepo {
    override suspend fun saveQuestionType(questionTypeEntity: QuestionTypeEntity): Long =
        dao.saveQuestionType(questionTypeEntity)
}
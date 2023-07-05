package com.example.proyectopacifico.domain.answerType

import com.example.proyectopacifico.data.localdb.answerType.AnswerTypeDao
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswerTypeEntity

class AnswerTypeRepoImpl(private val dao : AnswerTypeDao):AnswerTypeRepo {
    override suspend fun saveAnswerType(answerTypeEntity: AnswerTypeEntity): Long = dao.saveAnswerType(answerTypeEntity)
}
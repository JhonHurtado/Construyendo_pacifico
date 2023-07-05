package com.example.proyectopacifico.domain.answerType

import com.example.proyectopacifico.data.models.entities.arquitecture.AnswerTypeEntity

interface AnswerTypeRepo {
    suspend fun saveAnswerType(answerTypeEntity: AnswerTypeEntity):Long
}
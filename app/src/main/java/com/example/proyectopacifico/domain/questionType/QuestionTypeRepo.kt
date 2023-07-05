package com.example.proyectopacifico.domain.questionType

import com.example.proyectopacifico.data.models.entities.arquitecture.QuestionTypeEntity

interface QuestionTypeRepo {
    suspend fun saveQuestionType(questionTypeEntity: QuestionTypeEntity):Long
}
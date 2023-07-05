package com.example.proyectopacifico.domain.question

import com.example.proyectopacifico.data.models.entities.arquitecture.questions.QuestionEntity

interface QuestionRepo {
    suspend fun saveQuestion(questionEntity: QuestionEntity):Long
    suspend fun fetchAllQuestions(question_type_id : Int):List<QuestionEntity>
}
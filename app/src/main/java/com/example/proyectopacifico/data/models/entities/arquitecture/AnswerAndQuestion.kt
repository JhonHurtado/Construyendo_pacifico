package com.example.proyectopacifico.data.models.entities.arquitecture


data class AnswerAndQuestion(
    val id_answer: Int,
    val question_answer_id: String,
    val option_question_id: String,
    val place_id: Int,
    val open_answer: String,
    val id_question: Int,
    val module_type_id: Int,
    val answer_type_id: Int,
    val question_type_id: Int,
    val question_statement: String,
    val question_id: Int,
)

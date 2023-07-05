package com.example.proyectopacifico.data.models.entities.arquitecture.questions

data class OptionQuestionAndQuestion(
    val id_option_question: Int,
    val question_id: Int,
    val option_question_statement: String,
    val id_question: Int,
    val module_type_id: Int,
    val answer_type_id: Int,
    val question_type_id: Int,
    val question_statement: String,
    val question_father_id: Int,
)

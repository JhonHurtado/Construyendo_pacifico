package com.example.proyectopacifico.data.models.entities.arquitecture.questions

data class QuestionsAndSubQuestions(
    val id_question: Int,
    val module_type_id: Int,
    val answer_type_id: Int,
    val question_type_id: Int,
    val question_statement: String,
    val question_id: Int,
    var subQuestions:List<SubQuestion>?,
    var optionQuestion:List<OptionQuestionAndQuestion>?
)

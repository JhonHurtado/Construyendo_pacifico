package com.example.proyectopacifico.data.models.entities.arquitecture.questions

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuestionEntity(
    @PrimaryKey(autoGenerate = false)
    val id_question: Int,
    val module_type_id: Int,
    val answer_type_id: Int,
    val question_type_id: Int,
    val question_statement: String,
    val question_id: Int,
    )

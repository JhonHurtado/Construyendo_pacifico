package com.example.proyectopacifico.data.models.entities.arquitecture

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OptionQuestionEntity(
    @PrimaryKey(autoGenerate = false)
    val id_option_question: Int,
    val question_id: Int,
    val option_question_statement: String,
)

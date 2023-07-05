package com.example.proyectopacifico.data.models.entities.arquitecture

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AnswersEntity(
    @PrimaryKey(autoGenerate = true)
    val id_answer: Int,
    val question_id: String,
    val option_question_id: String,
    val place_id: Int,
    val open_answer: String,
)

package com.example.proyectopacifico.data.models.entities.arquitecture

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuestionTypeEntity(
    @PrimaryKey(autoGenerate = false)
    val id_question_type: Int,
    val question_type_name: String
)

package com.example.proyectopacifico.data.models.entities.arquitecture

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AnswerTypeEntity(
    @PrimaryKey(autoGenerate = false)
    val id_answer_type: Int,
    val answer_type_name: String
)

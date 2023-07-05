package com.example.proyectopacifico.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MeasureEntity(
    @PrimaryKey(autoGenerate = true)
    val id_measure: Int,
    val value: String,
    val register_date: String,
    val sample_id: Int,
)

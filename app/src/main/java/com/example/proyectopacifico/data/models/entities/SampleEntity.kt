package com.example.proyectopacifico.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SampleEntity(
    @PrimaryKey(autoGenerate = true)
    val id_sample: Int,
    val parameter_id: Int,
    val analysis_id: Int,
    val average: String,
)

package com.example.proyectopacifico.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ParameterEntity(
    @PrimaryKey(autoGenerate = false)
    val id_parameter: Int,
    val feature_id: Int,
    val parameter_name: String,
    val units: String,
    val expected_value: String,
    val operator: String,
)

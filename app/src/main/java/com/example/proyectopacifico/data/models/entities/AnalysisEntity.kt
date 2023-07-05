package com.example.proyectopacifico.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AnalysisEntity(
    @PrimaryKey(autoGenerate = true)
    val id_analysis: Int,
    val user_id: Int,
    val place_id: Int,
    val water_type_id: Int,
    val date: String,
    val hour: String,
    val sample_type: String,
    val surface_sources: String,
    val underground_sources: String,
    val catchment_type: String,
)

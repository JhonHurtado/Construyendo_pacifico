package com.example.proyectopacifico.data.models.web.analysis

data class Analysis(
    val catchment_type: String,
    val date: String,
    val hour: String,
    val id_analysis: Int,
    val place_id: Int,
    val sample_type: String,
    val surface_sources: String,
    val underground_sources: String,
    val user_id: Int,
    val water_type_id: Int
)
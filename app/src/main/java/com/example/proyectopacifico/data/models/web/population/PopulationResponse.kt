package com.example.proyectopacifico.data.models.web.population

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class PopulationResponse(
    val populated_center_id: Int,
    val longitude: String,
    val latitude: String,
    val photography: String,
    val inhabitants_number: String,
    val updated_at: String,
    val created_at: String,
    val id_population: Int
)
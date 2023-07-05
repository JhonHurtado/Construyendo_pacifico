package com.example.proyectopacifico.data.models.web.population

data class Population(
    val id_population: Int,
    val populated_center_id: Int,
    val longitude: String,
    val latitude: String,
    val photography: String,
    val inhabitants_number: String
)

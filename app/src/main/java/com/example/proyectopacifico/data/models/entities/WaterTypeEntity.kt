package com.example.proyectopacifico.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WaterTypeEntity(
    @PrimaryKey(autoGenerate = false)
    val id_water_type: Int,
    val water_type_name: String
)
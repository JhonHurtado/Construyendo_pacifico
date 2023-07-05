package com.example.proyectopacifico.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MunicipalityEntity(
    @PrimaryKey(autoGenerate = false)
    val id_municipality: Int,
    val department_id: Int,
    val municipality_name: String
)

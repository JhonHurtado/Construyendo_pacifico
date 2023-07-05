package com.example.proyectopacifico.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PopulationEthnicGroupEntity(
    @PrimaryKey(autoGenerate = true)
    val id_population_ethnic_group: Int,
    val ethnic_group_id: Int,
    val population_id: Int,
)

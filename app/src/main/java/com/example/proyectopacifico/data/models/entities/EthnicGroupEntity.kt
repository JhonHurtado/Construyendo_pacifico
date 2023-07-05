package com.example.proyectopacifico.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EthnicGroupEntity(
    @PrimaryKey(autoGenerate = false)
    val id_ethnic_group: Int,
    val ethnic_group_name: String
)
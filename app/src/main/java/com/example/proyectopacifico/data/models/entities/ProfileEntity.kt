package com.example.proyectopacifico.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProfileEntity(
    @PrimaryKey(autoGenerate = false)
    val id_profile: Int,
    val name_profile: String,
)
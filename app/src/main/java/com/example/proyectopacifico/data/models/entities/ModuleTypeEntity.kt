package com.example.proyectopacifico.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ModuleTypeEntity(
    @PrimaryKey(autoGenerate = false)
    val id_module_type: Int,
    val module_type_name: String
    )

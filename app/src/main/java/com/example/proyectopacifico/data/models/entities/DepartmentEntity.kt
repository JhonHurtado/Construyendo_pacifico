package com.example.proyectopacifico.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DepartmentEntity(
    @PrimaryKey(autoGenerate = false)
    val id_department: Int,
    val department_name: String
)
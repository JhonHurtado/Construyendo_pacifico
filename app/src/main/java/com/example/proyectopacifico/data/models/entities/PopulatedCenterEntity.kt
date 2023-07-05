package com.example.proyectopacifico.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PopulatedCenterEntity(
    @PrimaryKey(autoGenerate = false)
    val id_populated_center : Int ,
    val municipality_id : Int ,
    val populated_center_name : String ,
    val populated_center_type : String ,
    )

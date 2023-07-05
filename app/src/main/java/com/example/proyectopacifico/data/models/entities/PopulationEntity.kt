package com.example.proyectopacifico.data.models.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class PopulationEntity(
    @PrimaryKey(autoGenerate = true)
    val id_population: Int,
    val populated_center_id: Int,
    val longitude: String,
    val latitude: String,
    val photography: String,
    val inhabitants_number: String,
):Parcelable

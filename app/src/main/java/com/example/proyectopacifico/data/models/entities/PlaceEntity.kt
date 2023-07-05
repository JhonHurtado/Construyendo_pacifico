package com.example.proyectopacifico.data.models.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id_place: Int,
    val population_id: Int,
    val namePlace: String,
):Parcelable

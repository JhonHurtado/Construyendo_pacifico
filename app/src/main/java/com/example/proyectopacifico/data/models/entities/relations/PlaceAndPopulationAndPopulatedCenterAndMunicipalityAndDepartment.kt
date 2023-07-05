package com.example.proyectopacifico.data.models.entities.relations

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceAndPopulationAndPopulatedCenterAndMunicipalityAndDepartment(
    val id_place: Int,
    val population_id: Int,
    val namePlace: String,
    val id_population: Int,
    val populated_center_id: Int,
    val longitude: String,
    val latitude: String,
    val photography: String,
    val inhabitants_number: String,
    val id_populated_center : Int ,
    val municipality_id : Int ,
    val populated_center_name : String ,
    val populated_center_type : String ,
    val id_municipality: Int,
    val department_id: Int,
    val municipality_name: String,
    val id_department: Int,
    val department_name: String
):Parcelable

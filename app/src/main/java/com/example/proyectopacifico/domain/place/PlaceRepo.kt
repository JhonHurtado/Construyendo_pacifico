package com.example.proyectopacifico.domain.place

import com.example.proyectopacifico.data.models.entities.PlaceEntity
import com.example.proyectopacifico.data.models.entities.relations.PlaceAndPopulationAndPopulatedCenterAndMunicipalityAndDepartment
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.place.PlaceBody
import retrofit2.http.Body

interface PlaceRepo {
    //local
    suspend fun getPlacesByIdPopulation(idPopulation:Int): List<PlaceAndPopulationAndPopulatedCenterAndMunicipalityAndDepartment>
    suspend fun savePlace(placeEntity: PlaceEntity): Long
    suspend fun updatePlace(placeEntity: PlaceEntity): Int
    suspend fun deletePlace(placeEntity: PlaceEntity): Int
    suspend fun getAllPlaces(): List<PlaceEntity>

    //web
    suspend fun saveWebPlace(@Body placeBody: PlaceBody): BaseObjectResponse<Any>

}
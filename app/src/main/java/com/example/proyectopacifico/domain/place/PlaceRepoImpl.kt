package com.example.proyectopacifico.domain.place

import com.example.proyectopacifico.data.localdb.place.PlaceDao
import com.example.proyectopacifico.data.models.entities.PlaceEntity
import com.example.proyectopacifico.data.models.entities.relations.PlaceAndPopulationAndPopulatedCenterAndMunicipalityAndDepartment
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.place.PlaceBody
import com.example.proyectopacifico.data.rest.WebService

class PlaceRepoImpl(private val dao : PlaceDao,private val rest : WebService):PlaceRepo {
    //local
    override suspend fun getPlacesByIdPopulation(idPopulation:Int): List<PlaceAndPopulationAndPopulatedCenterAndMunicipalityAndDepartment> = dao.getPlacesByIdPopulation(idPopulation)
    override suspend fun savePlace(placeEntity: PlaceEntity): Long = dao.savePlace(placeEntity)
    override suspend fun updatePlace(placeEntity: PlaceEntity): Int = dao.updatePlace(placeEntity)
    override suspend fun deletePlace(placeEntity: PlaceEntity): Int = dao.deletePlace(placeEntity)
    override suspend fun getAllPlaces(): List<PlaceEntity> = dao.getAllPlaces()
    //web
    override suspend fun saveWebPlace(placeBody: PlaceBody): BaseObjectResponse<Any> = rest.saveWebPlace(placeBody)
}
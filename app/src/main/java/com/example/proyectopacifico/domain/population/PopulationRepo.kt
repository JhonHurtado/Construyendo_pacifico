package com.example.proyectopacifico.domain.population


import com.example.proyectopacifico.data.models.entities.PopulationEntity
import com.example.proyectopacifico.data.models.entities.relations.PopulationAndPopulatedCenterAndMunicipalityAndDepartment
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.population.PopulationBody
import com.example.proyectopacifico.data.models.web.population.PopulationResponse
import retrofit2.http.Body

interface PopulationRepo {
    // local db
    suspend fun getDbPopulations():List<PopulationAndPopulatedCenterAndMunicipalityAndDepartment>
    suspend fun savePopulation(populationsEntity: PopulationEntity):Long
    suspend fun deletePopulation(populationsEntity: PopulationEntity):Int
    suspend fun getPopulationByPopulatedCenter(id_populated_center:Int):PopulationEntity
    suspend fun updatePopulation(populationsEntity: PopulationEntity):Int
    suspend fun getAllPopulations(): List<PopulationEntity>

    //web
    suspend fun saveWebPopulation(populationBody: PopulationBody): BaseObjectResponse<PopulationResponse>


}
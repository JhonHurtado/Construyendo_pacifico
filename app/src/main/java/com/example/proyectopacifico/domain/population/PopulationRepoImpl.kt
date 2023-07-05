package com.example.proyectopacifico.domain.population

import com.example.proyectopacifico.data.localdb.population.PopulationDao
import com.example.proyectopacifico.data.models.entities.PopulationEntity
import com.example.proyectopacifico.data.models.entities.relations.PopulationAndPopulatedCenterAndMunicipalityAndDepartment
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.population.PopulationBody
import com.example.proyectopacifico.data.models.web.population.PopulationResponse
import com.example.proyectopacifico.data.rest.WebService

class PopulationRepoImpl(private val dao: PopulationDao,private val rest : WebService):PopulationRepo {
    //local db
    override suspend fun getDbPopulations(): List<PopulationAndPopulatedCenterAndMunicipalityAndDepartment> = dao.getPopulations()
    override suspend fun savePopulation(populationsEntity: PopulationEntity): Long = dao.savePopulation(populationsEntity)
    override suspend fun deletePopulation(populationsEntity: PopulationEntity): Int = dao.deletePopulation(populationsEntity)
    override suspend fun getPopulationByPopulatedCenter(id_populated_center:Int):PopulationEntity = dao.getPopulationByPopulatedCenter(id_populated_center)
    override suspend fun updatePopulation(populationsEntity: PopulationEntity):Int = dao.updatePopulation(populationsEntity)
    override suspend fun getAllPopulations(): List<PopulationEntity> = dao.getAllPopulations()

    //web server
    override suspend fun saveWebPopulation(populationBody: PopulationBody): BaseObjectResponse<PopulationResponse> = rest.saveWebPopulation(populationBody)
}
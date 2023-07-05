package com.example.proyectopacifico.domain.populationEthnicGroup

import com.example.proyectopacifico.data.localdb.populationEthnicGroup.PopulationEthnicGroupDao
import com.example.proyectopacifico.data.models.entities.PopulationEthnicGroupEntity
import com.example.proyectopacifico.data.models.entities.relations.PopulationAndEthnicGroups

class PopulationEthnicGroupRepoImpl(private val dao:PopulationEthnicGroupDao):PopulationEthnicGroupRepo {
    override suspend fun savePopulationEthnicGroup(populationEthnicGroupEntity: PopulationEthnicGroupEntity): Long = dao.savePopulationEthnicGroup(populationEthnicGroupEntity)
    override suspend fun getEthnicGroupsByPopulationId(id_population: Int): List<PopulationAndEthnicGroups> = dao.getEthnicGroupsByPopulationId(id_population)
    override suspend fun deleteEthnicGroupsByPopulationId(id_population:Int):Int = dao.deleteEthnicGroupsByPopulationId(id_population)

}
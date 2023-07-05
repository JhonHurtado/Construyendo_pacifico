package com.example.proyectopacifico.domain.populationEthnicGroup

import com.example.proyectopacifico.data.models.entities.PopulationEthnicGroupEntity
import com.example.proyectopacifico.data.models.entities.relations.PopulationAndEthnicGroups

interface PopulationEthnicGroupRepo {
    suspend fun savePopulationEthnicGroup(populationEthnicGroupEntity: PopulationEthnicGroupEntity):Long
    suspend fun getEthnicGroupsByPopulationId(id_population:Int):List<PopulationAndEthnicGroups>
    suspend fun deleteEthnicGroupsByPopulationId(id_population:Int):Int

}
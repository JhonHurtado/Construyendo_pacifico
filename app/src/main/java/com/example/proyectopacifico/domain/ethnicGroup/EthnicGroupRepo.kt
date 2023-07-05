package com.example.proyectopacifico.domain.ethnicGroup

import com.example.proyectopacifico.data.models.entities.EthnicGroupEntity

interface EthnicGroupRepo {
    suspend fun saveEthnicGroup(ethnicGroupEntity: EthnicGroupEntity):Long
    suspend fun getDbEthnicGroups():List<EthnicGroupEntity>
}
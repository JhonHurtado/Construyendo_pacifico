package com.example.proyectopacifico.domain.ethnicGroup

import com.example.proyectopacifico.data.localdb.ethnicGroup.EthnicGroupDao
import com.example.proyectopacifico.data.models.entities.EthnicGroupEntity

class EthnicGroupRepoImpl(private val dao : EthnicGroupDao):EthnicGroupRepo {
    override suspend fun saveEthnicGroup(ethnicGroupEntity: EthnicGroupEntity): Long =dao.saveEthnicGroup(ethnicGroupEntity)
    override suspend fun getDbEthnicGroups(): List<EthnicGroupEntity> = dao.getEthnicGroups()
}
package com.example.proyectopacifico.domain.populatedCenter

import com.example.proyectopacifico.data.localdb.populatedCenter.PopulatedCenterDao
import com.example.proyectopacifico.data.models.entities.PopulatedCenterEntity

class PopulatedCenterRepoImpl(private val dao : PopulatedCenterDao):PopulatedCenterRepo {
    override suspend fun saveDepartment(populatedCentersEntity: PopulatedCenterEntity): Long = dao.saveDepartment(populatedCentersEntity)
    override suspend fun getDbPopulatedCenters(id_municipality: Int): List<PopulatedCenterEntity> = dao.getPopulatedCenters(id_municipality)
}

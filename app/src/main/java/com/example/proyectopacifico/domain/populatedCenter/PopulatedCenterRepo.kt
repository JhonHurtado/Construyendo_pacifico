package com.example.proyectopacifico.domain.populatedCenter

import com.example.proyectopacifico.data.models.entities.PopulatedCenterEntity

interface PopulatedCenterRepo {
    suspend fun saveDepartment(populatedCentersEntity: PopulatedCenterEntity):Long
    suspend fun getDbPopulatedCenters(id_municipality:Int):List<PopulatedCenterEntity>
}
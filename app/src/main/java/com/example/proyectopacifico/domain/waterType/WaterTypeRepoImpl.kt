package com.example.proyectopacifico.domain.waterType

import com.example.proyectopacifico.data.localdb.waterType.WaterTypeDao
import com.example.proyectopacifico.data.models.entities.WaterTypeEntity

class WaterTypeRepoImpl(private val dao : WaterTypeDao) :WaterTypeRepo{
    override suspend fun saveWaterType(waterTypesEntity: WaterTypeEntity): Long = dao.saveWaterType(waterTypesEntity)
}
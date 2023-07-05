package com.example.proyectopacifico.domain.waterType

import com.example.proyectopacifico.data.models.entities.WaterTypeEntity

interface WaterTypeRepo {
    suspend fun saveWaterType(waterTypesEntity: WaterTypeEntity):Long
}
package com.example.proyectopacifico.domain.parameter

import com.example.proyectopacifico.data.models.entities.ParameterEntity

interface ParameterRepo {
    suspend fun saveParameter(parameterEntity: ParameterEntity):Long
    suspend fun getDbParameters():List<ParameterEntity>
}
package com.example.proyectopacifico.domain.parameter

import com.example.proyectopacifico.data.localdb.parameter.ParameterDao
import com.example.proyectopacifico.data.models.entities.ParameterEntity

class ParameterRepoImpl(private val dao: ParameterDao):ParameterRepo {
    override suspend fun saveParameter(parameterEntity: ParameterEntity): Long = dao.saveParameter(parameterEntity)
    override suspend fun getDbParameters(): List<ParameterEntity> = dao.getParameters()
}
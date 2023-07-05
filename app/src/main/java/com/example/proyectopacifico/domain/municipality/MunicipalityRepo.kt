package com.example.proyectopacifico.domain.municipality

import com.example.proyectopacifico.data.models.entities.MunicipalityEntity

interface MunicipalityRepo {
    suspend fun saveDepartment(municipalitiesEntity: MunicipalityEntity):Long
    suspend fun getDbDepartments(id_department:Int):List<MunicipalityEntity>
}
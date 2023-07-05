package com.example.proyectopacifico.domain.municipality


import com.example.proyectopacifico.data.localdb.municipality.MunicipalityDao
import com.example.proyectopacifico.data.models.entities.MunicipalityEntity


class MunicipalityRepoImpl(private val dao : MunicipalityDao):MunicipalityRepo {
    override suspend fun saveDepartment(municipalitiesEntity: MunicipalityEntity): Long = dao.saveDepartment(municipalitiesEntity)
    override suspend fun getDbDepartments(id_department:Int):List<MunicipalityEntity> = dao.getDepartments(id_department)
}
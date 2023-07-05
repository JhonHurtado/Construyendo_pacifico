package com.example.proyectopacifico.domain.moduleType

import com.example.proyectopacifico.data.localdb.moduleType.ModuleTypeDao
import com.example.proyectopacifico.data.models.entities.ModuleTypeEntity

class ModuleTypeRepoImpl(private val dao:ModuleTypeDao):ModuleTypeRepo {
    override suspend fun saveModuleType(moduleTypeEntity: ModuleTypeEntity): Long = dao.saveModuleType(moduleTypeEntity)
}
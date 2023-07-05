package com.example.proyectopacifico.domain.moduleType

import com.example.proyectopacifico.data.models.entities.ModuleTypeEntity

interface ModuleTypeRepo {
    suspend fun saveModuleType(moduleTypeEntity: ModuleTypeEntity):Long
}
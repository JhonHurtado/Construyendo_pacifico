package com.example.proyectopacifico.domain.department

import com.example.proyectopacifico.data.models.entities.DepartmentEntity

interface DepartmentRepo {
    suspend fun saveDepartment(departmentEntity: DepartmentEntity):Long
    suspend fun getDbDepartments():List<DepartmentEntity>
}
package com.example.proyectopacifico.domain.department

import com.example.proyectopacifico.data.localdb.department.DepartmentDao
import com.example.proyectopacifico.data.models.entities.DepartmentEntity

class DepartmentRepoImpl(private val dao: DepartmentDao):DepartmentRepo {
    override suspend fun saveDepartment(departmentEntity: DepartmentEntity):Long = dao.saveDepartment(departmentEntity)
    override suspend fun getDbDepartments(): List<DepartmentEntity> = dao.getDepartments()
}
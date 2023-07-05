package com.example.proyectopacifico.data.localdb.department

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectopacifico.data.models.entities.DepartmentEntity

@Dao
interface DepartmentDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveDepartment(departmentEntity: DepartmentEntity):Long

    @Query("SELECT * FROM departmententity")
    suspend fun getDepartments():List<DepartmentEntity>
}
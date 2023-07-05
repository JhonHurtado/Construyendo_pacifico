package com.example.proyectopacifico.data.localdb.municipality

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectopacifico.data.models.entities.MunicipalityEntity

@Dao
interface MunicipalityDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveDepartment(municipalitiesEntity: MunicipalityEntity):Long

    @Query("SELECT * FROM MunicipalityEntity WHERE department_id = :id_department")
    suspend fun getDepartments(id_department:Int):List<MunicipalityEntity>
}
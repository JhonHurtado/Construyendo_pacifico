package com.example.proyectopacifico.data.localdb.moduleType

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.proyectopacifico.data.models.entities.ModuleTypeEntity

@Dao
interface ModuleTypeDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveModuleType(moduleTypeEntity: ModuleTypeEntity):Long
}
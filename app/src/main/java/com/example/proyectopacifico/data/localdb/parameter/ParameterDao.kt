package com.example.proyectopacifico.data.localdb.parameter

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectopacifico.data.models.entities.ParameterEntity

@Dao
interface ParameterDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveParameter(parameterEntity: ParameterEntity):Long

    @Query("SELECT * FROM parameterentity")
    suspend fun getParameters():List<ParameterEntity>

}
package com.example.proyectopacifico.data.localdb.waterType

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.proyectopacifico.data.models.entities.WaterTypeEntity

@Dao
interface WaterTypeDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveWaterType(waterTypesEntity: WaterTypeEntity):Long
}
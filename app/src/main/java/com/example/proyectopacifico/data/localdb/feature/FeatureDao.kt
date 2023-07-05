package com.example.proyectopacifico.data.localdb.feature

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.proyectopacifico.data.models.entities.FeatureEntity

@Dao
interface FeatureDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveFeatures(featureEntity: FeatureEntity):Long
}
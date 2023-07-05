package com.example.proyectopacifico.data.localdb.populatedCenter

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectopacifico.data.models.entities.PopulatedCenterEntity

@Dao
interface PopulatedCenterDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveDepartment(populatedCentersEntity: PopulatedCenterEntity):Long

    @Query("SELECT * FROM populatedcenterentity WHERE municipality_id  = :id_municipality")
    suspend fun getPopulatedCenters(id_municipality:Int):List<PopulatedCenterEntity>
}
package com.example.proyectopacifico.data.localdb.ethnicGroup

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectopacifico.data.models.entities.EthnicGroupEntity

@Dao
interface EthnicGroupDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveEthnicGroup(ethnicGroupEntity: EthnicGroupEntity):Long

    @Query("SELECT * FROM ethnicgroupentity")
    suspend fun getEthnicGroups():List<EthnicGroupEntity>
}
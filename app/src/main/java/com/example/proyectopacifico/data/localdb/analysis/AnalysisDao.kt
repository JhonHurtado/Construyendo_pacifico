package com.example.proyectopacifico.data.localdb.analysis

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.Query
import com.example.proyectopacifico.data.models.entities.AnalysisEntity

@Dao
interface AnalysisDao {

    @Query("SELECT * FROM analysisentity ")
    suspend fun getAllAnalysis():List<AnalysisEntity>

    @Insert(onConflict = ABORT)
    suspend fun saveAnalysis(analysisEntity: AnalysisEntity):Long

    @Query("SELECT * FROM analysisentity WHERE place_id = :id_place")
    suspend fun getOneAnalysis(id_place:Int):AnalysisEntity
}
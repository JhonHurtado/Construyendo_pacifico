package com.example.proyectopacifico.data.localdb.measure

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.Query
import com.example.proyectopacifico.data.models.entities.MeasureEntity
import com.example.proyectopacifico.data.models.entities.PopulationEntity


@Dao
interface MeasureDao {
    @Query("SELECT * FROM measureentity")
    suspend fun getAllMeasures():List<MeasureEntity>

    @Insert(onConflict = ABORT)
    suspend fun saveMeasure(measureEntity: MeasureEntity):Long

    @Query("SELECT * FROM measureentity where sample_id = :sample_id")
    suspend fun getMeasuresBySample(sample_id:Int):List<MeasureEntity>

    @Query("delete from measureentity where sample_id = :sample_id")
    suspend fun deleteMeasuresById(sample_id:Int):Int

    @Delete
    suspend fun deleteMeasure(measureEntity: MeasureEntity):Int

}
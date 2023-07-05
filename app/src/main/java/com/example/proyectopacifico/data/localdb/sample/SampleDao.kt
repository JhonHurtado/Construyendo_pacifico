package com.example.proyectopacifico.data.localdb.sample

import androidx.room.*
import androidx.room.OnConflictStrategy.ABORT
import com.example.proyectopacifico.data.models.entities.SampleEntity
import com.example.proyectopacifico.data.models.entities.relations.SampleParameterAndMeasure

@Dao
interface SampleDao {

    @Query("SELECT * FROM sampleentity")
    suspend fun getAllSamples():List<SampleEntity>

    @Query("SELECT * FROM sampleentity WHERE analysis_id = :analysis_id")
    suspend fun getSampleByAnalysisId(analysis_id:Int): List<SampleEntity>

    @Query("SELECT * FROM sampleentity WHERE analysis_id = :analysis_id AND parameter_id = :parameter_id")
    suspend fun getExistingSample(analysis_id:Int,parameter_id:Int): SampleEntity

    @Insert(onConflict = ABORT)
    suspend fun saveSample(SamplesEntity: SampleEntity):Long

    @Query("SELECT * FROM sampleentity ORDER BY id_sample DESC LIMIT 1 ")
    suspend fun getLastSample():SampleEntity

    @Transaction
    @Query("select * from sampleentity as s join  parameterentity as p on s.parameter_id = p.id_parameter where analysis_id = :id_analysis")
    suspend fun getSampleParametersAndMeasures(id_analysis:Int):List<SampleParameterAndMeasure>

    @Update
    suspend fun updateSample(samplesEntity: SampleEntity):Int

    @Query("delete from sampleentity where id_sample = :id_sample")
    suspend fun deleteSampleById(id_sample:Int):Int

}
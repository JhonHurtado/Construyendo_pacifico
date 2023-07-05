package com.example.proyectopacifico.domain.sample

import com.example.proyectopacifico.data.models.entities.SampleEntity
import com.example.proyectopacifico.data.models.entities.relations.SampleParameterAndMeasure
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.sample.SampleBody
import retrofit2.http.Body


interface SampleRepo {
    //local db
    suspend fun getAllSamples():List<SampleEntity>
    suspend fun getSampleByAnalysisId(analysis_id:Int): List<SampleEntity>
    suspend fun saveSample(SamplesEntity: SampleEntity):Long
    suspend fun getExistingSample(analysis_id:Int,parameter_id:Int):SampleEntity
    suspend fun getLastSample():SampleEntity
    suspend fun getSampleParametersAndMeasures(id_analysis:Int):List<SampleParameterAndMeasure>
    suspend fun updateSample(samplesEntity: SampleEntity):Int
    suspend fun deleteSampleById(id_sample:Int):Int

    //web
    suspend fun saveWebSample(sampleBody: SampleBody): BaseObjectResponse<Any>
}
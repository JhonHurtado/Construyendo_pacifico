package com.example.proyectopacifico.domain.sample

import com.example.proyectopacifico.data.localdb.sample.SampleDao
import com.example.proyectopacifico.data.models.entities.SampleEntity
import com.example.proyectopacifico.data.models.entities.relations.SampleParameterAndMeasure
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.sample.SampleBody
import com.example.proyectopacifico.data.rest.WebService


class SampleRepoImpl(private val dao: SampleDao,private val rest : WebService):SampleRepo {
    //local db
    override suspend fun getAllSamples(): List<SampleEntity> = dao.getAllSamples()
    override suspend fun getSampleByAnalysisId(analysis_id:Int): List<SampleEntity> = dao.getSampleByAnalysisId(analysis_id)
    override suspend fun saveSample(SamplesEntity: SampleEntity): Long =dao.saveSample(SamplesEntity)
    override suspend fun getExistingSample(analysis_id: Int, parameter_id: Int): SampleEntity =dao.getExistingSample(analysis_id,parameter_id)
    override suspend fun getLastSample():SampleEntity = dao.getLastSample()
    override suspend fun getSampleParametersAndMeasures(id_analysis:Int):List<SampleParameterAndMeasure> = dao.getSampleParametersAndMeasures(id_analysis)
    override suspend fun updateSample(samplesEntity: SampleEntity):Int = dao.updateSample(samplesEntity)
    override suspend fun deleteSampleById(id_sample:Int):Int = dao.deleteSampleById(id_sample)

    //web
    override suspend fun saveWebSample(sampleBody: SampleBody): BaseObjectResponse<Any> = rest.saveWebSample(sampleBody)
}
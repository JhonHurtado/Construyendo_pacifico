package com.example.proyectopacifico.domain.measure

import com.example.proyectopacifico.data.localdb.measure.MeasureDao
import com.example.proyectopacifico.data.models.entities.MeasureEntity
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.measure.MeasureBody
import com.example.proyectopacifico.data.rest.WebService


class MeasureRepoImpl(private val dao: MeasureDao,private val rest : WebService) :MeasureRepo{
    override suspend fun getAllMeasures(): List<MeasureEntity> = dao.getAllMeasures()
    override suspend fun saveMeasure(measureEntity: MeasureEntity): Long = dao.saveMeasure(measureEntity)
    override suspend fun getMeasuresBySample(sample_id: Int): List<MeasureEntity> = dao.getMeasuresBySample(sample_id)
    override suspend fun deleteMeasuresById(sample_id:Int):Int = dao.deleteMeasuresById(sample_id)
    override suspend fun deleteMeasure(measureEntity: MeasureEntity):Int = dao.deleteMeasure(measureEntity)
    override suspend fun saveWebMeasure(measureBody: MeasureBody): BaseObjectResponse<Any> = rest.saveWebMeasure(measureBody)
}
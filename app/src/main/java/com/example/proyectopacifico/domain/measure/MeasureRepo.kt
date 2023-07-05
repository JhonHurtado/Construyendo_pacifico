package com.example.proyectopacifico.domain.measure

import com.example.proyectopacifico.data.models.entities.MeasureEntity
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.measure.MeasureBody
import retrofit2.http.Body


interface MeasureRepo {
    //local db
    suspend fun getAllMeasures():List<MeasureEntity>
    suspend fun saveMeasure(measureEntity: MeasureEntity):Long
    suspend fun getMeasuresBySample(sample_id:Int):List<MeasureEntity>
    suspend fun deleteMeasuresById(sample_id:Int):Int
    suspend fun deleteMeasure(measureEntity: MeasureEntity):Int
    //web
    suspend fun saveWebMeasure(measureBody: MeasureBody): BaseObjectResponse<Any>

}
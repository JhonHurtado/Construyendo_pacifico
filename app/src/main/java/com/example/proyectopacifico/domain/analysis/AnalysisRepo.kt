package com.example.proyectopacifico.domain.analysis

import com.example.proyectopacifico.data.models.entities.AnalysisEntity
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.analysis.AnalysisBody
import retrofit2.http.Body


interface AnalysisRepo {
    //local db
    suspend fun getAllAnalysis():List<AnalysisEntity>
    suspend fun getOneAnalysis(id_place:Int): AnalysisEntity
    suspend fun saveAnalysis(analysisEntity: AnalysisEntity):Long

    //web
    suspend fun saveWebAnalysis(analysisBody: AnalysisBody): BaseObjectResponse<Any>
}
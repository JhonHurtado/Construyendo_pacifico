package com.example.proyectopacifico.domain.analysis


import com.example.proyectopacifico.data.localdb.analysis.AnalysisDao
import com.example.proyectopacifico.data.models.entities.AnalysisEntity
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.analysis.AnalysisBody
import com.example.proyectopacifico.data.rest.WebService
import retrofit2.Retrofit

class AnalysisRepoImpl(private val dao: AnalysisDao,private val rest: WebService): AnalysisRepo {
    //local db
    override suspend fun getAllAnalysis(): List<AnalysisEntity> = dao.getAllAnalysis()
    override suspend fun getOneAnalysis(id_place: Int): AnalysisEntity = dao.getOneAnalysis(id_place)
    override suspend fun saveAnalysis(analysisEntity: AnalysisEntity): Long = dao.saveAnalysis(analysisEntity)

    //web
    override suspend fun saveWebAnalysis(analysisBody: AnalysisBody): BaseObjectResponse<Any> = rest.saveWebAnalysis(analysisBody)
}
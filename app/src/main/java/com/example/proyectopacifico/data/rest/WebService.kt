package com.example.proyectopacifico.data.rest

import com.example.proyectopacifico.core.Constants
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.analysis.AnalysisBody
import com.example.proyectopacifico.data.models.web.answers.AnswerBody
import com.example.proyectopacifico.data.models.web.measure.MeasureBody
import com.example.proyectopacifico.data.models.web.place.PlaceBody
import com.example.proyectopacifico.data.models.web.population.PopulationBody
import com.example.proyectopacifico.data.models.web.population.PopulationResponse
import com.example.proyectopacifico.data.models.web.sample.SampleBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface WebService {

    @POST("almacenar_poblaciones")
    suspend fun saveWebPopulation(@Body populationBody: PopulationBody): BaseObjectResponse<PopulationResponse>

    @POST("almacenar_lugares")
    suspend fun saveWebPlace(@Body placeBody: PlaceBody): BaseObjectResponse<Any>

    @POST("almacenar_respuestas")
    suspend fun saveWebAnswers(@Body answerBody: AnswerBody): BaseObjectResponse<Any>

    @POST("almacenar_analysis")
    suspend fun saveWebAnalysis(@Body analysisBody: AnalysisBody): BaseObjectResponse<Any>

    @POST("almacenar_muestras")
    suspend fun saveWebSample(@Body sampleBody: SampleBody): BaseObjectResponse<Any>

    @POST("almacenar_medidas")
    suspend fun saveWebMeasure(@Body measureBody: MeasureBody): BaseObjectResponse<Any>

}
object RetrofitClient{
    val webService: WebService by lazy {

        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WebService::class.java)
    }
}
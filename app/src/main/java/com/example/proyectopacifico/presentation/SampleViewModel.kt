package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.SampleEntity
import com.example.proyectopacifico.data.models.entities.relations.SampleParameterAndMeasure
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.sample.SampleBody
import com.example.proyectopacifico.domain.sample.SampleRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class SamplesViewModel(private val repo : SampleRepo) :ViewModel(){
    //local db
    fun saveSample(samplesEntity: SampleEntity): StateFlow<Result<Long>> = flow{
        kotlin.runCatching {
            repo.saveSample(samplesEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getExistingSample(analysis_id: Int, parameter_id: Int): StateFlow<Result<SampleEntity>> = flow{
        kotlin.runCatching {
            repo.getExistingSample(analysis_id,parameter_id)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getLastSample(): StateFlow<Result<SampleEntity>> = flow{
        kotlin.runCatching {
            repo.getLastSample()
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getSampleByAnalysisId(analysis_id:Int): StateFlow<Result<List<SampleEntity>>> = flow{
        kotlin.runCatching {
            repo.getSampleByAnalysisId(analysis_id)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getSampleParametersAndMeasures(analysis_id:Int): StateFlow<Result<List<SampleParameterAndMeasure>>> = flow{
        kotlin.runCatching {
            repo.getSampleParametersAndMeasures(analysis_id)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun updateSample(samplesEntity: SampleEntity): StateFlow<Result<Int>> = flow{
        kotlin.runCatching {
            repo.updateSample(samplesEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun deleteSampleById(id_sample:Int): StateFlow<Result<Int>> = flow{
        kotlin.runCatching {
            repo.deleteSampleById(id_sample)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getAllSamples(): StateFlow<Result<List<SampleEntity>>> = flow{
        kotlin.runCatching {
            repo.getAllSamples()
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())


    //web

    fun saveWebSample(sampleBody: SampleBody): StateFlow<Result<BaseObjectResponse<Any>>> = flow{
        kotlin.runCatching {
            repo.saveWebSample(sampleBody)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())


}

class SamplesViewModelFactory(private val repo : SampleRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(SampleRepo::class.java).newInstance(repo)
    }
}
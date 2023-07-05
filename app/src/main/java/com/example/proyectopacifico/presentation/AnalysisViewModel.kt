package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.data.models.entities.AnalysisEntity
import com.example.proyectopacifico.domain.analysis.AnalysisRepo
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.analysis.AnalysisBody
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class AnalysisViewModel(private val repo: AnalysisRepo):ViewModel() {
    //local db
    fun getOneAnalysis(id_place:Int): StateFlow<Result<AnalysisEntity>> = flow{
        kotlin.runCatching {
            repo.getOneAnalysis(id_place)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun saveAnalysis(analysisEntity: AnalysisEntity): StateFlow<Result<Long>> = flow{
        kotlin.runCatching {
            repo.saveAnalysis(analysisEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getAllAnalysis(): StateFlow<Result<List<AnalysisEntity>>> = flow{
        kotlin.runCatching {
            repo.getAllAnalysis()
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    //web

    fun saveWebAnalysis(analysisBody: AnalysisBody): StateFlow<Result<BaseObjectResponse<Any>>> = flow{
        kotlin.runCatching {
            repo.saveWebAnalysis(analysisBody)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

}
class AnalysisViewModelFactory(private val repo : AnalysisRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(AnalysisRepo::class.java).newInstance(repo)
    }
}
package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.MeasureEntity
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.measure.MeasureBody
import com.example.proyectopacifico.domain.measure.MeasureRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class MeasureViewModel(private val repo: MeasureRepo):ViewModel() {
    //local db
    fun saveMeasure(measureEntity: MeasureEntity): StateFlow<Result<Long>> = flow{
        kotlin.runCatching {
            repo.saveMeasure(measureEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getMeasuresBySample(sample_id: Int): StateFlow<Result<List<MeasureEntity>>> = flow{
        kotlin.runCatching {
            repo.getMeasuresBySample(sample_id)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun deleteMeasuresById(sample_id: Int): StateFlow<Result<Int>> = flow{
        kotlin.runCatching {
            repo.deleteMeasuresById(sample_id)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun deleteMeasure(measureEntity: MeasureEntity): StateFlow<Result<Int>> = flow{
        kotlin.runCatching {
            repo.deleteMeasure(measureEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getAllMeasures(): StateFlow<Result<List<MeasureEntity>>> = flow{
        kotlin.runCatching {
            repo.getAllMeasures()
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    //web
    fun saveWebMeasure(measureBody: MeasureBody): StateFlow<Result<BaseObjectResponse<Any>>> = flow{
        kotlin.runCatching {
            repo.saveWebMeasure(measureBody)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())


}

class MeasureViewModelFactory(private val repo : MeasureRepo) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(MeasureRepo::class.java).newInstance(repo)
    }
}


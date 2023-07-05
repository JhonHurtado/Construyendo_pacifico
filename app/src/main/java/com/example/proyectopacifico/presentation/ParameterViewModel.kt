package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.ParameterEntity
import com.example.proyectopacifico.domain.parameter.ParameterRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class ParameterViewModel(private val repo : ParameterRepo):ViewModel() {
    fun saveParameter(parameterEntity: ParameterEntity): StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            repo.saveParameter(parameterEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getDbParameters(): StateFlow<Result<List<ParameterEntity>>> = flow {
        kotlin.runCatching {
            repo.getDbParameters()
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())
}
class ParameterViewModelFactory(private val repo : ParameterRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ParameterRepo::class.java).newInstance(repo)
    }
}

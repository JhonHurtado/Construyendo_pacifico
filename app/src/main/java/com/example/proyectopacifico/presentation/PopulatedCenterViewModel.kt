package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.PopulatedCenterEntity
import com.example.proyectopacifico.domain.populatedCenter.PopulatedCenterRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class PopulatedCenterViewModel(private val repo : PopulatedCenterRepo):ViewModel() {
    fun savePopulatedCenter(populatedCentersEntity: PopulatedCenterEntity): StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            repo.saveDepartment(populatedCentersEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())


    fun getDbPopulatedCenters(id_municipality: Int): StateFlow<Result<List<PopulatedCenterEntity>>> = flow {
        kotlin.runCatching {
            repo.getDbPopulatedCenters(id_municipality)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

}
class PopulatedCenterViewModelFactory(private val repo : PopulatedCenterRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(PopulatedCenterRepo::class.java).newInstance(repo)

    }
}
package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.data.models.entities.PopulationEthnicGroupEntity
import com.example.proyectopacifico.domain.populationEthnicGroup.PopulationEthnicGroupRepo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.relations.PopulationAndEthnicGroups
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class PopulationEthnicGroupViewModel(private val repo: PopulationEthnicGroupRepo) : ViewModel() {
    fun savePopulationEthnicGroup(populatedEthnicGroupEntity: PopulationEthnicGroupEntity): StateFlow<Result<Long>> =
        flow {
            kotlin.runCatching {
                repo.savePopulationEthnicGroup(populatedEthnicGroupEntity)
            }.onSuccess {
                emit(Result.Success(it))
            }.onFailure {
                emit(Result.Failure(Exception(it.message)))
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),Result.Loading())

    fun getEthnicGroupsByPopulationId(id_population: Int):StateFlow<Result<List<PopulationAndEthnicGroups>>> = flow{
        kotlin.runCatching {
            repo.getEthnicGroupsByPopulationId(id_population)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),Result.Loading())

    fun deleteEthnicGroupsByPopulationId(id_population: Int):StateFlow<Result<Int>> = flow{
        kotlin.runCatching {
            repo.deleteEthnicGroupsByPopulationId(id_population)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),Result.Loading())



}



class PopulationEthnicGroupViewModelFactory(private val repo : PopulationEthnicGroupRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(PopulationEthnicGroupRepo::class.java).newInstance(repo)

    }
}
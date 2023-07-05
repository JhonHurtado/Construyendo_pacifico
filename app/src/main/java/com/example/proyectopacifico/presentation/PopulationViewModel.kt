package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.PopulationEntity
import com.example.proyectopacifico.data.models.entities.relations.PopulationAndPopulatedCenterAndMunicipalityAndDepartment
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.population.PopulationBody
import com.example.proyectopacifico.data.models.web.population.PopulationResponse
import com.example.proyectopacifico.domain.population.PopulationRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class PopulationViewModel(private val repo : PopulationRepo):ViewModel() {
    // Local DataBase

    fun getDbPopulations(): StateFlow<Result<List<PopulationAndPopulatedCenterAndMunicipalityAndDepartment>>> = flow{
        kotlin.runCatching {
            repo.getDbPopulations()
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun savePopulation(populationsEntity: PopulationEntity): StateFlow<Result<Long>> = flow{
        kotlin.runCatching {
            repo.savePopulation(populationsEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun updatePopulation(populationsEntity: PopulationEntity): StateFlow<Result<Int>> = flow{
        kotlin.runCatching {
            repo.updatePopulation(populationsEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun deletePopulation(populationsEntity: PopulationEntity): StateFlow<Result<Int>> = flow{
        kotlin.runCatching {
            repo.deletePopulation(populationsEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getPopulationByPopulatedCenter(id_populated_center:Int): StateFlow<Result<PopulationEntity>> = flow{
        kotlin.runCatching {
            repo.getPopulationByPopulatedCenter(id_populated_center)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getAllPopulations(): StateFlow<Result<List<PopulationEntity>>> = flow{
        kotlin.runCatching {
            repo.getAllPopulations()
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    // Web Services

    fun saveWebPopulation(populationBody: PopulationBody): StateFlow<Result<BaseObjectResponse<PopulationResponse>>> = flow{
        kotlin.runCatching {
            repo.saveWebPopulation(populationBody)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

}
class PopulationViewModelFactory(private val repo : PopulationRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(PopulationRepo::class.java).newInstance(repo)

    }
}


package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.PlaceEntity
import com.example.proyectopacifico.data.models.entities.PopulationEntity
import com.example.proyectopacifico.data.models.entities.relations.PlaceAndPopulationAndPopulatedCenterAndMunicipalityAndDepartment
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.place.PlaceBody
import com.example.proyectopacifico.domain.place.PlaceRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class PlaceViewModel (private val repo : PlaceRepo): ViewModel() {

    fun getPlaces(idPopulation:Int): StateFlow<Result<List<PlaceAndPopulationAndPopulatedCenterAndMunicipalityAndDepartment>>> = flow {
        kotlin.runCatching {
            repo.getPlacesByIdPopulation(idPopulation)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun savePlace(placeEntity: PlaceEntity): StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            repo.savePlace(placeEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun updatePlace(placeEntity: PlaceEntity): StateFlow<Result<Int>> = flow{
        kotlin.runCatching {
            repo.updatePlace(placeEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun deletePlace(placeEntity: PlaceEntity): StateFlow<Result<Int>> = flow{
        kotlin.runCatching {
            repo.deletePlace(placeEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getAllPlaces(): StateFlow<Result<List<PlaceEntity>>> = flow {
        kotlin.runCatching {
            repo.getAllPlaces()
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())


    //web

    fun saveWebPlace(placeBody: PlaceBody): StateFlow<Result<BaseObjectResponse<Any>>> = flow {
        kotlin.runCatching {
            repo.saveWebPlace(placeBody)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())


}

class PlaceViewModelFactory(private val repo : PlaceRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(PlaceRepo::class.java).newInstance(repo)
    }
}
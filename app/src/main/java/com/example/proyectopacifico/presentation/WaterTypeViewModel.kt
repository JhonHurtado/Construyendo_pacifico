package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.WaterTypeEntity
import com.example.proyectopacifico.domain.waterType.WaterTypeRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class WaterTypeViewModel(private val repo : WaterTypeRepo):ViewModel(){
    fun saveWaterType(waterTypesEntity: WaterTypeEntity): StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            repo.saveWaterType(waterTypesEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())
}
class WaterTypeViewModelFactory(private val repo : WaterTypeRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(WaterTypeRepo::class.java).newInstance(repo)
    }
}
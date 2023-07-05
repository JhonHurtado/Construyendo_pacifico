package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.FeatureEntity
import com.example.proyectopacifico.domain.feature.FeatureRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class FeatureViewModel(private val repo : FeatureRepo):ViewModel() {

    fun saveFeatures(featureEntity: FeatureEntity): StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            repo.saveFeatures(featureEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())
}
class FeatureViewModelFactory(private val repo : FeatureRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(FeatureRepo::class.java).newInstance(repo)
    }
}

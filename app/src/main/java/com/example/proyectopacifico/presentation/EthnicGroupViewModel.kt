package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.EthnicGroupEntity
import com.example.proyectopacifico.domain.ethnicGroup.EthnicGroupRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class EthnicGroupViewModel(private val repo : EthnicGroupRepo):ViewModel() {

    fun saveEthnicGroup(ethnicGroupEntity: EthnicGroupEntity): StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            repo.saveEthnicGroup(ethnicGroupEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getDbEthnicGroups(): StateFlow<Result<List<EthnicGroupEntity>>> = flow {
        kotlin.runCatching {
            repo.getDbEthnicGroups()
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())


}
class EthnicGroupViewModelFactory(private val repo : EthnicGroupRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(EthnicGroupRepo::class.java).newInstance(repo)
    }
}


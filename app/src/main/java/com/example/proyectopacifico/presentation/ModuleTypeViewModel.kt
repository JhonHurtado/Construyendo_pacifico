package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.ModuleTypeEntity
import com.example.proyectopacifico.domain.moduleType.ModuleTypeRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class ModuleTypeViewModel (private val repo : ModuleTypeRepo): ViewModel() {
    fun saveModuleType(moduleTypeEntity: ModuleTypeEntity): StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            repo.saveModuleType(moduleTypeEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())
}
class ModuleTypeViewModelFactory(private val repo : ModuleTypeRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ModuleTypeRepo::class.java).newInstance(repo)
    }
}
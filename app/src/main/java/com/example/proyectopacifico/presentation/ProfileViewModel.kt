package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.ProfileEntity
import com.example.proyectopacifico.domain.profile.ProfileRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(private val repo : ProfileRepo):ViewModel() {
    fun saveProfile(profileEntity: ProfileEntity): StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            repo.saveProfile(profileEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())
}
class ProfileViewModelFactory(private val repo : ProfileRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ProfileRepo::class.java).newInstance(repo)
    }
}
package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswerTypeEntity
import com.example.proyectopacifico.domain.answerType.AnswerTypeRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class AnswerTypeViewModel(private val repo : AnswerTypeRepo):ViewModel() {
    fun saveAnswerType(answerTypeEntity: AnswerTypeEntity): StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            repo.saveAnswerType(answerTypeEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())
}
class AnswerTypeViewModelFactory(private val repo : AnswerTypeRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(AnswerTypeRepo::class.java).newInstance(repo)
    }
}
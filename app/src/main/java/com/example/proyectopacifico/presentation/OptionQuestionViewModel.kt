package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.arquitecture.OptionQuestionEntity
import com.example.proyectopacifico.data.models.entities.arquitecture.questions.OptionQuestionAndQuestion
import com.example.proyectopacifico.domain.optionQuestion.OptionQuestionRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class OptionQuestionViewModel (private val repo : OptionQuestionRepo): ViewModel() {
    fun saveOptionQuestion(optionQuestionEntity: OptionQuestionEntity): StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            repo.saveOptionQuestion(optionQuestionEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun fetchOptionQuestionAndQuestionByQuestionId(question_id: Int): StateFlow<Result<List<OptionQuestionAndQuestion>>> = flow {
        kotlin.runCatching {
            repo.fetchOptionQuestionAndQuestionByQuestionId(question_id)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())
}

class OptionQuestionViewModelFactory(private val repo : OptionQuestionRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(OptionQuestionRepo::class.java).newInstance(repo)
    }
}
package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.arquitecture.QuestionTypeEntity
import com.example.proyectopacifico.domain.questionType.QuestionTypeRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class QuestionTypeViewModel (private val repo : QuestionTypeRepo): ViewModel() {
    fun saveQuestionType(questionTypeEntity: QuestionTypeEntity): StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            repo.saveQuestionType(questionTypeEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())
}
class QuestionTypeViewModelFactory(private val repo : QuestionTypeRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(QuestionTypeRepo::class.java).newInstance(repo)
    }
}
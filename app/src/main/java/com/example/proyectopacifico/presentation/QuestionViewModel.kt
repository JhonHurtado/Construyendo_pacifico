package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswersEntity
import com.example.proyectopacifico.data.models.entities.arquitecture.questions.QuestionEntity
import com.example.proyectopacifico.domain.question.QuestionRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class QuestionViewModel (private val repo : QuestionRepo): ViewModel() {
    fun saveQuestion(questionEntity: QuestionEntity): StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            repo.saveQuestion(questionEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun fetchAllQuestions(question_type_id : Int): StateFlow<Result<List<QuestionEntity>>> = flow {
        kotlin.runCatching {
            repo.fetchAllQuestions(question_type_id)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

}

class QuestionViewModelFactory(private val repo : QuestionRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(QuestionRepo::class.java).newInstance(repo)
    }
}
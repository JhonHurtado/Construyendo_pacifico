package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswerAndQuestion
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswersEntity
import com.example.proyectopacifico.data.models.web.BaseObjectResponse
import com.example.proyectopacifico.data.models.web.answers.AnswerBody
import com.example.proyectopacifico.domain.answer.AnswerRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class AnswerViewModel(private val repo: AnswerRepo) : ViewModel() {
    //local db

    fun saveAnswer(answersEntity: AnswersEntity): StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            repo.saveAnswer(answersEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun deleteAnswer(answersEntity: AnswersEntity): StateFlow<Result<Int>> = flow {
        kotlin.runCatching {
            repo.deleteAnswer(answersEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun fetchAnswersByPlaceId(id_place: Int): StateFlow<Result<List<AnswerAndQuestion>>> = flow {
        kotlin.runCatching {
            repo.fetchAnswersByPlaceId(id_place)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getAllAnswers(): StateFlow<Result<List<AnswersEntity>>> = flow {
        kotlin.runCatching {
            repo.getAllAnswers()
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    //web

    fun saveWebAnswers(answerBody: AnswerBody): StateFlow<Result<BaseObjectResponse<Any>>> = flow {
        kotlin.runCatching {
            repo.saveWebAnswers(answerBody)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())
}

class AnswerViewModelFactory(private val repo: AnswerRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(AnswerRepo::class.java).newInstance(repo)
    }
}
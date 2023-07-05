package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.DepartmentEntity
import com.example.proyectopacifico.domain.department.DepartmentRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class DepartmentViewModel(private val repo : DepartmentRepo):ViewModel() {

    fun saveDepartment(departmentEntity: DepartmentEntity):StateFlow<Result<Long>> = flow{
        kotlin.runCatching {
            repo.saveDepartment(departmentEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getDbDepartments():StateFlow<Result<List<DepartmentEntity>>> = flow{
        kotlin.runCatching {
            repo.getDbDepartments()
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())


}
class DepartmentViewModelFactory(private val repo : DepartmentRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(DepartmentRepo::class.java).newInstance(repo)

    }
}

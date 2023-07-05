package com.example.proyectopacifico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.MunicipalityEntity
import com.example.proyectopacifico.domain.municipality.MunicipalityRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class MunicipalityViewModel (private val repo : MunicipalityRepo):ViewModel(){
    fun saveDepartment(municipalitiesEntity: MunicipalityEntity): StateFlow<Result<Long>> = flow{
        kotlin.runCatching {
            repo.saveDepartment(municipalitiesEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getDbDepartments(id_department:Int): StateFlow<Result<List<MunicipalityEntity>>> = flow{
        kotlin.runCatching {
            repo.getDbDepartments(id_department)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

}
class MunicipalityViewModelFactory(private val repo : MunicipalityRepo) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(MunicipalityRepo::class.java).newInstance(repo)
    }
}


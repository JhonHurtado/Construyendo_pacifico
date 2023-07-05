package com.example.proyectopacifico.data.localdb.answerType

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswerTypeEntity

@Dao
interface AnswerTypeDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveAnswerType(answerTypeEntity: AnswerTypeEntity):Long
}
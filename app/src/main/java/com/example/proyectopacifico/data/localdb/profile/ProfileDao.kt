package com.example.proyectopacifico.data.localdb.profile

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import com.example.proyectopacifico.data.models.entities.ProfileEntity

@Dao
interface ProfileDao {
    @Insert(onConflict = ABORT)
    suspend fun saveProfile(profileEntity: ProfileEntity):Long
}
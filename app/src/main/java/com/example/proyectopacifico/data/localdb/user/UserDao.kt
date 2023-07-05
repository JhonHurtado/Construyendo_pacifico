package com.example.proyectopacifico.data.localdb.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectopacifico.data.models.entities.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveUser(usersEntity: UserEntity):Long

    @Query("SELECT * FROM userentity WHERE user_nick = :userNick")
    suspend fun getOneUser(userNick:String):UserEntity
}
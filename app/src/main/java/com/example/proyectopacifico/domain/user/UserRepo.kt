package com.example.proyectopacifico.domain.user

import com.example.proyectopacifico.data.models.entities.UserEntity


interface UserRepo {
    suspend fun saveUser(usersEntity: UserEntity):Long
    suspend fun getOneUser(userNick:String):UserEntity
}
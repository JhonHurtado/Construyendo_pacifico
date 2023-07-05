package com.example.proyectopacifico.domain.user


import com.example.proyectopacifico.data.localdb.user.UserDao
import com.example.proyectopacifico.data.models.entities.UserEntity

class UserRepoImpl(private val dao : UserDao) :UserRepo{
    override suspend fun saveUser(usersEntity: UserEntity): Long = dao.saveUser(usersEntity)
    override suspend fun getOneUser(userNick: String): UserEntity = dao.getOneUser(userNick)
}
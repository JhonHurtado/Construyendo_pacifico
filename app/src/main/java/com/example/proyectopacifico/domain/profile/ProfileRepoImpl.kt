package com.example.proyectopacifico.domain.profile

import com.example.proyectopacifico.data.localdb.profile.ProfileDao
import com.example.proyectopacifico.data.models.entities.ProfileEntity

class ProfileRepoImpl(private val dao : ProfileDao):ProfileRepo{
    override suspend fun saveProfile(profileEntity: ProfileEntity): Long = dao.saveProfile(profileEntity)
}
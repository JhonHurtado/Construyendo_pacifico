package com.example.proyectopacifico.domain.profile

import com.example.proyectopacifico.data.models.entities.ProfileEntity


interface ProfileRepo {
    suspend fun saveProfile(profileEntity: ProfileEntity):Long
}
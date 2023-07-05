package com.example.proyectopacifico.domain.feature

import com.example.proyectopacifico.data.models.entities.FeatureEntity

interface FeatureRepo {
    suspend fun saveFeatures(featureEntity: FeatureEntity):Long
}
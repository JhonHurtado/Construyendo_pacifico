package com.example.proyectopacifico.domain.feature

import com.example.proyectopacifico.data.localdb.feature.FeatureDao
import com.example.proyectopacifico.data.models.entities.FeatureEntity

class FeatureRepoImpl(private val dao : FeatureDao) :FeatureRepo{
    override suspend fun saveFeatures(featureEntity: FeatureEntity): Long = dao.saveFeatures(featureEntity)
}
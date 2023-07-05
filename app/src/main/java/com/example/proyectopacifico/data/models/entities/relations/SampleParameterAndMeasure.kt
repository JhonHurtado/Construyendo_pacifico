package com.example.proyectopacifico.data.models.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.proyectopacifico.data.models.entities.MeasureEntity

data class SampleParameterAndMeasure(
    @Embedded
    val sampleParameter : SampleAndParameter,
    @Relation(
        parentColumn = "id_sample",
        entityColumn = "sample_id"
    )
    val measures : List<MeasureEntity>
)

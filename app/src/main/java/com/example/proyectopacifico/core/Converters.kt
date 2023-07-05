package com.example.proyectopacifico.core

import com.example.proyectopacifico.data.models.entities.*
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswersEntity
import com.example.proyectopacifico.data.models.web.analysis.Analysis
import com.example.proyectopacifico.data.models.web.analysis.AnalysisBody
import com.example.proyectopacifico.data.models.web.answers.AnswerBody
import com.example.proyectopacifico.data.models.web.answers.AnswersArchitecture
import com.example.proyectopacifico.data.models.web.measure.Measure
import com.example.proyectopacifico.data.models.web.measure.MeasureBody
import com.example.proyectopacifico.data.models.web.place.Place
import com.example.proyectopacifico.data.models.web.place.PlaceBody
import com.example.proyectopacifico.data.models.web.population.Population
import com.example.proyectopacifico.data.models.web.population.PopulationBody
import com.example.proyectopacifico.data.models.web.sample.Sample
import com.example.proyectopacifico.data.models.web.sample.SampleBody

fun List<PopulationEntity>.toPopulationBody(populationBody: PopulationBody): PopulationBody {
    this.forEach {
        populationBody.populations.add(
            Population(
                it.id_population,
                it.populated_center_id,
                it.longitude,
                it.latitude,
                it.photography,
                it.inhabitants_number,
            )
        )
    }
    return populationBody
}

fun List<PlaceEntity>.toPlaceBody(placeBody: PlaceBody): PlaceBody {
    this.forEach {
        placeBody.places.add(
            Place(
                it.id_place,
                it.namePlace,
                it.population_id,
            )
        )
    }
    return placeBody
}

fun List<AnswersEntity>.toAnswerBody(answerBody: AnswerBody): AnswerBody {
    this.forEach {
        answerBody.answers_architecture.add(
            AnswersArchitecture(
                it.open_answer,
                it.option_question_id.toInt(),
                it.place_id,
                it.question_id.toInt(),
            )
        )
    }
    return answerBody
}
fun List<AnalysisEntity>.toAnalysisBody(analysisBody:AnalysisBody): AnalysisBody {
    this.forEach {
        analysisBody.analysis.add(
            Analysis(
                it.catchment_type,
                it.date,
                it.hour,
                it.id_analysis,
                it.place_id,
                it.sample_type,
                it.surface_sources,
                it.underground_sources,
                it.user_id,
                it.water_type_id,
            )
        )
    }
    return analysisBody
}

fun List<SampleEntity>.toSampleBody(sampleBody: SampleBody): SampleBody {
    this.forEach {
        sampleBody.samples.add(
            Sample(
                it.analysis_id,
                it.average,
                it.id_sample,
                it.parameter_id,
            )
        )
    }
    return sampleBody
}

fun List<MeasureEntity>.toMeasureBody(measureBody: MeasureBody): MeasureBody {
    this.forEach {
        measureBody.measures.add(
            Measure(
                it.id_measure,
                it.register_date,
                it.sample_id,
                it.value,
            )
        )
    }
    return measureBody
}
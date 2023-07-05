package com.example.proyectopacifico.domain.optionQuestion

import com.example.proyectopacifico.data.models.entities.arquitecture.OptionQuestionEntity
import com.example.proyectopacifico.data.models.entities.arquitecture.questions.OptionQuestionAndQuestion

interface OptionQuestionRepo {
    suspend fun saveOptionQuestion(optionQuestionEntity: OptionQuestionEntity):Long
    suspend fun fetchOptionQuestionAndQuestionByQuestionId(question_id:Int):List<OptionQuestionAndQuestion>
}
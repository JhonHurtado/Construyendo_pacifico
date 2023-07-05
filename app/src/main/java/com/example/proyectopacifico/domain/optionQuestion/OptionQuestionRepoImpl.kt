package com.example.proyectopacifico.domain.optionQuestion

import com.example.proyectopacifico.data.localdb.optionQuestion.OptionQuestionDao
import com.example.proyectopacifico.data.models.entities.arquitecture.OptionQuestionEntity
import com.example.proyectopacifico.data.models.entities.arquitecture.questions.OptionQuestionAndQuestion

class OptionQuestionRepoImpl(private val dao : OptionQuestionDao):OptionQuestionRepo {
    override suspend fun saveOptionQuestion(optionQuestionEntity: OptionQuestionEntity): Long = dao.saveOptionQuestion(optionQuestionEntity)
    override suspend fun fetchOptionQuestionAndQuestionByQuestionId(question_id: Int): List<OptionQuestionAndQuestion> = dao.fetchOptionQuestionAndQuestionByQuestionId(question_id)
}
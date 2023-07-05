package com.example.proyectopacifico.data.models.entities.arquitecture

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class AnswersAndQuestions(
    val answersAndQuestions: @RawValue List<AnswerAndQuestion>
):Parcelable

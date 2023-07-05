package com.example.proyectopacifico.data.models.web

data class BaseObjectResponse<T>(
    val status: String = "",
    val action: String = "",
    val show: String = "",
    val message: String = "",
    val delay: String = "",
    val code: String = "",
    val results: T ?= null
)
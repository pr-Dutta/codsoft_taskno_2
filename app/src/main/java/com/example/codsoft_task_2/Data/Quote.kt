package com.example.codsoft_task_2.Data

import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val id: Int,
    val quote: String?,
    val author: String
)

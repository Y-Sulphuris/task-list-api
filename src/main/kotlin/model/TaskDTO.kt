package com.example.model

import kotlinx.serialization.Serializable


@Serializable
data class TaskDTO(
    val id: Int,
    val title: String,
    val description: String? = null,
    val completed: Boolean = false
)

@Serializable
data class TaskCreateVM(
    val title: String,
    val description: String? = null,
    val completed: Boolean = false
)

@Serializable
data class TaskUpdateVM(
    val title: String? = null,
    val description: String? = null,
    val completed: Boolean? = null
)

package com.markoala.tomoandroid.data.model.moim

data class MoimList(
    val title: String,
    val description: String,
    val peopleCount: Int = 1,
    val leader: Boolean,
    val createdAt: String?,
)

data class MoimListDTO(
    val title: String,
    val description: String,
    val peopleCount: Int,
    val leader: Boolean,
    val createdAt: String
)

data class CreateMoimDTO(
    val title: String,
    val description: String,
    val emails: List<String>
)

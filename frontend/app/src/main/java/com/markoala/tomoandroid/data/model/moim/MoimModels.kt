package com.markoala.tomoandroid.data.model.moim

data class Meeting(
    val title: String,
    val location: String?,
    val time: String?,
    val peopleCounts: Int = 1
)

data class MoimDTO(
    val moimName: String,
    val description: String,
    val peopleCounts: Int
)

data class CreateMoimDTO(
    val moimName: String,
    val description: String,
    val emails: List<String>
)

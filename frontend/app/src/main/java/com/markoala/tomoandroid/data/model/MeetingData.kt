package com.markoala.tomoandroid.data.model


data class Meeting(
    val title: String,
    val location: String?,
    val time: String?,
    val peopleCounts: Int = 1
)

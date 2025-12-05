package com.markoala.tomoandroid.data.model

data class CreatePromiseDTO(
    val title: String,
    val promiseName: String,
    val promiseDate: String,   // "2025-10-11"
    val promiseTime: String,   // "10:30:00"
    val place: String
)
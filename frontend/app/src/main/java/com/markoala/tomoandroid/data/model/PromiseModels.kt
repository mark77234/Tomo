package com.markoala.tomoandroid.data.model

import com.google.gson.annotations.SerializedName

data class PromiseResponseDTO(
    val promiseName: String,
    val promiseDate: String,
    val promiseTime: String,
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("place") // 레거시 호환
    private val legacyPlace: String? = null
) {
    val resolvedLocation: String
        get() = location?.takeIf { it.isNotBlank() } ?: legacyPlace.orEmpty()
}

data class PromiseDTO(
    val title: String,
    val promiseName: String,
    val promiseDate: String,
    val promiseTime: String,
    val place: String
)

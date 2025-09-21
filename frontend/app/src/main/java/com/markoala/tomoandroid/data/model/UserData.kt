package com.markoala.tomoandroid.data.model

data class UserData(
    val uuid: String,
    val email: String,
    val username: String
)

data class PostResponse(
    val success: Boolean,
    val message: String
)

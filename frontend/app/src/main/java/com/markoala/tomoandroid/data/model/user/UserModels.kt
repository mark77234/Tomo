package com.markoala.tomoandroid.data.model.user

data class UserProfile(
    val uuid: String,
    val email: String,
    val username: String
)

data class BaseResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T
)
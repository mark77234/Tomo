package com.markoala.tomoandroid.data.model

data class UserData(
    val uuid: String,
    val email: String,
    val username: String
)

data class PostExampleResponse(
    val success: Boolean,
    val message: String
    // 서버 응답에 맞게 필요한 필드 추가
)

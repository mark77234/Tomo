package com.markoala.tomoandroid.data.model

data class FriendProfile(
    val username: String,
    val email: String,
    val friendship: Int,
    val createdAt: String
)

data class FriendSummary(
    val username: String,
    val email: String,
    val uuid: String? = null
)

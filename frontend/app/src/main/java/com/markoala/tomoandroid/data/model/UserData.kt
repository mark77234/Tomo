package com.markoala.tomoandroid.data.model

data class UserData(
    val uuid: String,
    val email: String,
    val username: String
)

data class FriendProfile(
    val name: String,
    val email: String,
    val friendship: Int,
    val createdAt: String
)

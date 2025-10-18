package com.markoala.tomoandroid.data.model.friends

data class FriendProfile(
    val username: String,
    val email: String,
    val friendship: Double,
    val createdAt: String
)

data class FriendSummary(
    val username: String,
    val email: String
)

data class FriendSearchRequest(
    val email: String
)

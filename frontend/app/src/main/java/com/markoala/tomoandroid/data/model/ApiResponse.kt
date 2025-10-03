package com.markoala.tomoandroid.data.model

data class PostResponse(
    val success: Boolean,
    val message: String
)

data class FriendData(
    val username: String,
    val email: String
)

data class GetFriendsResponse(
    val success: Boolean,
    val message: String,
    val data: FriendData
)
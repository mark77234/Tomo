package com.markoala.tomoandroid.data.model

data class FriendSearchRequest(
    val email: String
)

data class FriendSearchData(
    val username: String,
    val email: String
)

data class FriendSearchResponse(
    val success: Boolean,
    val message: String,
    val data: FriendSearchData?
)

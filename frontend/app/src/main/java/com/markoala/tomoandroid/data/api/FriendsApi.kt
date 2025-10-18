package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.friends.FriendListResponse
import com.markoala.tomoandroid.data.model.friends.FriendLookupResponse
import com.markoala.tomoandroid.data.model.friends.FriendSearchRequest
import com.markoala.tomoandroid.data.model.friends.FriendSearchResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FriendsApiService {
    @POST("/public/friends")
    fun postFriends(
        @Body body: FriendSearchRequest
    ): Call<FriendSearchResponse>

    @GET("/public/friends")
    fun getFriends(
        @Query("email") email: String
    ): Call<FriendLookupResponse>

    @DELETE("/public/friends")
    fun deleteFriends(
        @Query("friendEmail") email: String
    ): Call<FriendLookupResponse>

    @GET("/public/friends/list")
    fun getFriendsList(): Call<FriendListResponse>
}

val friendsApiService: FriendsApiService by lazy {
    ApiClient.create(FriendsApiService::class.java)
}

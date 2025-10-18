package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.FriendSearchRequest
import com.markoala.tomoandroid.data.model.FriendSearchResponse
import com.markoala.tomoandroid.data.model.FriendsListDTO
import com.markoala.tomoandroid.data.model.FriendsResponseDTO
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
    ): Call<FriendsResponseDTO>

    @DELETE("/public/friends")
    fun deleteFriends(
        @Query("friendEmail") email: String
    ): Call<FriendsResponseDTO>

    @GET("/public/friends/list")
    fun getFriendsList(): Call<FriendsListDTO>
}

val friendsApiService: FriendsApiService by lazy {
    ApiClient.create(FriendsApiService::class.java)
}

package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.*
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FriendsApi {
    @POST("/public/friends")
    fun postFriends(
        @Query("query") query: String
    ): Call<BaseResponse<FriendSummary?>>

    @GET("/public/friends")
    fun getFriends(
        @Query("query") query: String
    ): Call<BaseResponse<FriendSummary>>

    @GET("/public/friends/detail")
    fun getFriendDetails(
        @Query("query") query: String
    ): Call<BaseResponse<FriendProfile>>

    @DELETE("/public/friends")
    fun deleteFriends(
        @Query("friendEmail") email: String
    ): Call<BaseResponse<FriendSummary>>

    @GET("/public/friends/list")
    fun getFriendsList(): Call<BaseResponse<List<FriendProfile>>>
}

val friendsApi: FriendsApi by lazy {
    ApiClient.create(FriendsApi::class.java)
}

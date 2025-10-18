package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.friends.FriendProfile
import com.markoala.tomoandroid.data.model.friends.FriendSearchRequest
import com.markoala.tomoandroid.data.model.friends.FriendSummary
import com.markoala.tomoandroid.data.model.user.BaseResponse
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
    ): Call<BaseResponse<FriendSummary?>>

    @GET("/public/friends")
    fun getFriends(
        @Query("email") email: String
    ): Call<BaseResponse<FriendSummary>>

    @DELETE("/public/friends")
    fun deleteFriends(
        @Query("friendEmail") email: String
    ): Call<BaseResponse<FriendSummary>>

    @GET("/public/friends/list")
    fun getFriendsList(): Call<BaseResponse<List<FriendProfile>>>
}

val friendsApiService: FriendsApiService by lazy {
    ApiClient.create(FriendsApiService::class.java)
}

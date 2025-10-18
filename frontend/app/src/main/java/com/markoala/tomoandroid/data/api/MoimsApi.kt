package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.friends.FriendSummary
import com.markoala.tomoandroid.data.model.moim.MoimDTO
import com.markoala.tomoandroid.data.model.user.BaseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MoimsApi {

    @GET("/public/friends")
    fun getFriends(
        @Query("email") email: String
    ): Call<BaseResponse<FriendSummary>>

    @GET("/public/moims")
    fun getMoims(
        @Query("moimName") moimName: String
    ): Call<BaseResponse<MoimDTO>>

}


val MoimsApiService: MoimsApi by lazy {
    ApiClient.create(MoimsApi::class.java)
}
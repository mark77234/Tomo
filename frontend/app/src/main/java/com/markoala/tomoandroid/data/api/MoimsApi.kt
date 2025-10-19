package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.moim.CreateMoimDTO
import com.markoala.tomoandroid.data.model.moim.MoimDTO
import com.markoala.tomoandroid.data.model.user.BaseResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MoimsApi {

    @GET("/public/moims")
    fun getMoims(
        @Query("moimName") moimName: String
    ): Call<BaseResponse<MoimDTO>>

    @GET("/public/moims/list")
    fun getMoimsList(): Call<BaseResponse<List<MoimDTO>>>

    @POST("/public/moims")
    fun postMoim(
        @Body body: CreateMoimDTO
    ): Call<BaseResponse<Unit>>
}


val MoimsApiService: MoimsApi by lazy {
    ApiClient.create(MoimsApi::class.java)
}
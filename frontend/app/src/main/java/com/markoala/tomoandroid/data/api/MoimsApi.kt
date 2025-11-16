package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.moim.CreateMoimDTO
import com.markoala.tomoandroid.data.model.moim.MoimDetails
import com.markoala.tomoandroid.data.model.moim.MoimListDTO
import com.markoala.tomoandroid.data.model.user.BaseResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MoimsApi {

    @GET("/public/moims/{moim_id}")
    fun getMoimDetails(
        @Path("moim_id") moimId: Int
    ): Call<BaseResponse<MoimDetails>>

    @GET("/public/moims/list")
    fun getMoimsList(): Call<BaseResponse<List<MoimListDTO>>>

    @POST("/public/moims")
    fun postMoim(
        @Body body: CreateMoimDTO
    ): Call<BaseResponse<Unit>>

    @DELETE("/public/moims/{moim_id}")
    fun deleteMoim(
        @Path("moim_id") moimId: Int
    ): Call<BaseResponse<Unit>>
}


val MoimsApiService: MoimsApi by lazy {
    ApiClient.create(MoimsApi::class.java)
}
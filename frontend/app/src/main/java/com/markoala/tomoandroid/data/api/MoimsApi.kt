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
import retrofit2.http.Query

interface MoimsApi {

    @GET("/public/moims")
    fun getMoims(
        @Query("moimName") moimName: String
    ): Call<BaseResponse<MoimListDTO>>

    @GET("/public/moims/list")
    fun getMoimsList(): Call<BaseResponse<List<MoimListDTO>>>

    @GET("/public/moims")
    fun getMoimDetails(
        @Query("moimTitle") moimTitle:String
    ): Call<BaseResponse<MoimDetails>>

    @POST("/public/moims")
    fun postMoim(
        @Body body: CreateMoimDTO
    ): Call<BaseResponse<Unit>>

    @DELETE("/public/moims/{title}")
    fun deleteMoim(
        @Path("title") title: String
    ): Call<BaseResponse<Unit>>
}


val MoimsApiService: MoimsApi by lazy {
    ApiClient.create(MoimsApi::class.java)
}
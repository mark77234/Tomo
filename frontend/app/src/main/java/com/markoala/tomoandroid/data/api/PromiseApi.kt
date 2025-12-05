package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PromiseApi {
    @POST ("/public/promises")
        fun createPromise(
            @Body body: PromiseDTO
        ): Call<BaseResponse<Unit>>


    @GET ("public/moims/promises")
        fun getPromisesList(
            @Query("moimName")  moimName: String
        ):  Call<BaseResponse<List<PromiseResponseDTO>>>
}


val PromiseApiService: PromiseApi by lazy {
    ApiClient.create(PromiseApi::class.java)
}
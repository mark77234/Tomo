package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PromiseApi {
    @POST ("/public/promises")
        fun createPromise(
            @Body body: CreatePromiseDTO
        ): Call<BaseResponse<Unit>>
}


val PromiseApiService: MoimsApi by lazy {
    ApiClient.create(MoimsApi::class.java)
}
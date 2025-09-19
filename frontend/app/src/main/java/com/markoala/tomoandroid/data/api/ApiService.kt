package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.PostExampleResponse
import com.markoala.tomoandroid.data.model.UserData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {
    // GET 예시
    @GET("users")
    fun getExample(@Query("param") param: String): Call<String>

    // POST 예시
    @POST("sign")
    fun postExample(@Body body: UserData): Call<PostExampleResponse>

    // PUT 예시
    @PUT("users/{id}")
    fun putExample(@Body body: UserData, @Query("id") id: String): Call<String>

    // DELETE 예시
    @DELETE("users/{id}")
    fun deleteExample(@Query("id") id: String): Call<String>
}

val retrofit = Retrofit.Builder()
    .baseUrl("https://dad075f4-0834-4273-afcc-1b1b584b0ce8.mock.pstmn.io/") // 서버 URL 입력
    .addConverterFactory(GsonConverterFactory.create()) // JSON 컨버터 추가
    .build()
val apiService = retrofit.create(ApiService::class.java)

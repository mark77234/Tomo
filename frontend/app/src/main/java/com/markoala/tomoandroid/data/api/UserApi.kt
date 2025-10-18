package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.FirebaseTokenResponse
import com.markoala.tomoandroid.data.model.PostResponse
import com.markoala.tomoandroid.data.model.UserData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface UserApiService {
    @POST("/public/signup")
    fun signup(@Body body: UserData): Call<PostResponse>

    // Firebase ID 토큰으로 access token과 refresh token 받기
    @POST("/api/auth/firebase-login")
    suspend fun getTokensWithFirebaseToken(
        @Header("Authorization") firebaseToken: String,
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<FirebaseTokenResponse>
}

val userApiService: UserApiService by lazy {
    ApiClient.create(UserApiService::class.java)
}

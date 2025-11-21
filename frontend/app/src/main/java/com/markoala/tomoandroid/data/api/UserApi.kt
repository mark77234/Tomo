package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.auth.AuthTokenBundle
import com.markoala.tomoandroid.data.model.user.BaseResponse
import com.markoala.tomoandroid.data.model.user.UserProfile
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST

interface UserApi {
    @POST("/api/auth/refresh")
    suspend fun refreshToken(
        @Header("Refresh-Token") refreshToken: String,
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<BaseResponse<AuthTokenBundle>>
    @POST("/public/signup")
    fun signup(@Body body: UserProfile): Call<BaseResponse<Unit>>

    // Firebase ID 토큰으로 access token과 refresh token 받기
    @POST("/api/auth/firebase-login")
    suspend fun getTokensWithFirebaseToken(
        @Header("Authorization") firebaseToken: String,
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<BaseResponse<AuthTokenBundle>>

    @DELETE("/public/users")
    fun deleteUser(): Call<BaseResponse<Unit>>

    @DELETE("/public/logout")
    fun logout(): Call<BaseResponse<Unit>>
}

val userApi: UserApi by lazy {
    ApiClient.create(UserApi::class.java)
}

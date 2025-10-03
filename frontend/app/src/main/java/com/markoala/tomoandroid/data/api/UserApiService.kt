package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.FriendSearchRequest
import com.markoala.tomoandroid.data.model.FriendSearchResponse
import com.markoala.tomoandroid.data.model.PostResponse
import com.markoala.tomoandroid.data.model.UserData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface UserApiService {
    // POST 예시
    @POST("/sign")
    fun signup(@Body body: UserData): Call<PostResponse>

    // 친구 검색 API
    @POST("/friends")
    fun searchFriend(
        @Header("Authorization") authorization: String,
        @Body body: FriendSearchRequest
    ): Call<FriendSearchResponse>
}

val retrofit = Retrofit.Builder()
    .baseUrl("https://8e38e9e2b1b0.ngrok-free.app") // 서버 URL 입력
    .addConverterFactory(GsonConverterFactory.create()) // JSON 컨버터 추가
    .build()
val apiService = retrofit.create(UserApiService::class.java)

// https://8e38e9e2b1b0.ngrok-free.app/ <- ngrok 주소
// https://dad075f4-0834-4273-afcc-1b1b584b0ce8.mock.pstmn.io <- postman mock server
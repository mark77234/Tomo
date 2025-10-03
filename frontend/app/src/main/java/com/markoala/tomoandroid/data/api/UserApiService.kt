package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.FriendSearchRequest
import com.markoala.tomoandroid.data.model.FriendSearchResponse
import com.markoala.tomoandroid.data.model.GetFriendsResponse
import com.markoala.tomoandroid.data.model.PostResponse
import com.markoala.tomoandroid.data.model.UserData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApiService {
    // POST 예시
    @POST("/sign")
    fun signup(@Body body: UserData): Call<PostResponse>

    // 친구 검색 API
    @POST("/friends")
    fun postFriends(
        @Header("Authorization") authorization: String,
        @Body body: FriendSearchRequest
    ): Call<FriendSearchResponse>

    @GET("/friends")
    fun getFriends(
        @Header("Authorization") authorization: String,
        @Query("email") email: String
    ): Call<GetFriendsResponse>
}

val retrofit = Retrofit.Builder()
    .baseUrl("https://27dae586598e.ngrok-free.app/") // 서버 URL 입력
    .addConverterFactory(GsonConverterFactory.create()) // JSON 컨버터 추가
    .build()
val apiService = retrofit.create(UserApiService::class.java)

package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.data.model.FirebaseTokenResponse
import com.markoala.tomoandroid.data.model.FriendSearchRequest
import com.markoala.tomoandroid.data.model.FriendSearchResponse
import com.markoala.tomoandroid.data.model.FriendsListDTO
import com.markoala.tomoandroid.data.model.FriendsResponseDTO
import com.markoala.tomoandroid.data.model.PostResponse
import com.markoala.tomoandroid.data.model.UserData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApiService {
    // POST 예시
    @POST("/sign")
    fun signup(@Body body: UserData): Call<PostResponse>

    // Firebase ID 토큰으로 access token과 refresh token 받기
    @POST("/api/auth/firebase-login")
    suspend fun getTokensWithFirebaseToken(
        @Header("Authorization") firebaseToken: String,
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<FirebaseTokenResponse>

    // 친구 검색 API (AuthInterceptor가 자동으로 Authorization 헤더 추가)
    @POST("/public/friends")
    fun postFriends(
        @Body body: FriendSearchRequest
    ): Call<FriendSearchResponse>

    @GET("/public/friends")
    fun getFriends(
        @Query("email") email: String
    ): Call<FriendsResponseDTO>

    @DELETE("/public/friends")
    fun deleteFriends(
        @Query("friendEmail") email: String
    ): Call<FriendsResponseDTO>

    @GET("/public/friends/list")
    fun getFriendsList(
    ): Call<FriendsListDTO>
}

// HttpLoggingInterceptor 생성 및 설정
private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY // 요청과 응답의 헤더 및 본문 모두 로깅
}

// AuthInterceptor를 포함한 OkHttpClient 생성
private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor) // 로깅 인터셉터 추가
    .addInterceptor(AuthInterceptor()) // 인터셉터(자동으로 헤더에 토큰 추가)
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl("http://13.209.55.252:8080/") // 서버 URL 입력
    .client(okHttpClient) // 인증 인터셉터 포함시키니 HTTP 클라이언트 연결
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create()) // JSON <-> Kotlin 객체 자동 변환
    .build()
val apiService =
    retrofit.create(UserApiService::class.java) // apiService 인터페이스를 구현체로 자동 생성 (apiService.signup(), .. 등등)

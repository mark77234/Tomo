package com.markoala.tomoandroid.data.api

import com.markoala.tomoandroid.auth.AuthManager
import okhttp3.Interceptor
import okhttp3.Response

// 모든 네트워크 요청에 자동으로 인증 토큰(Access Token)을 추가하기 위한 OkHttp의 인터셉터
// Retrofit에서 API 요청을 보낼 때마다 매번 수동으로 Authorization 헤더를 붙이지 않아도 되는 역할
class AuthInterceptor :
    Interceptor { // Interceptor는 OkHttp의 기능(인터페이스)로 요청 혹은 응답을 가로채서 수정하거나 로그를 추가할 수 있음
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request() // 현재 전송될 원본 HTTP 요청을 가져옴

        // 이미 Authorization 헤더가 있는 경우 그대로 진행 (Firebase 토큰 교환 시)
        if (originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        // 저장된 access token 가져오기
        val accessToken = AuthManager.getStoredAccessToken()

        return if (accessToken != null) { // Authorization 헤더에 추가
            val newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            chain.proceed(newRequest) // access token 있는 경우 헤더 붙여서 요청 진행
        } else {
            chain.proceed(originalRequest) // 없는 경우 원본 요청 진행
        }
    }
}

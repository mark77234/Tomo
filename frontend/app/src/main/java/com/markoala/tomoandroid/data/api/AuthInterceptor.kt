package com.markoala.tomoandroid.data.api

import android.util.Log
import com.markoala.tomoandroid.auth.AuthManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    companion object {
        private const val TAG = "AuthInterceptor"

        @Volatile
        private var isRefreshing = false
        private val refreshLock = Object()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // 1) refresh 요청은 건드리지 않음
        val isRefreshCall = request.header("Refresh-Token") != null
        if (isRefreshCall) {
            return chain.proceed(request)
        }

        // 2) 항상 최신 토큰을 헤더에 세팅
        val latestToken = AuthManager.getStoredAccessToken()
        if (latestToken != null) {
            request = request.newBuilder()
                .removeHeader("Authorization")
                .addHeader("Authorization", "Bearer $latestToken")
                .build()
        }

        // 이 요청이 실제로 보낼 때 사용한 토큰 (나중에 비교용)
        val tokenUsedInRequest = request.header("Authorization")
            ?.removePrefix("Bearer ")
            ?: ""

        var response = chain.proceed(request)

        // 3) 401 아니면 그냥 반환
        if (response.code != 401) {
            return response
        }

        // 4) 401 → 응답 닫고 토큰 갱신 로직으로
        response.close()

        synchronized(refreshLock) {
            val currentToken = AuthManager.getStoredAccessToken()

            // (중요) 이 시점에,
            // 내가 요청 보낼 때 쓴 토큰과, 지금 저장된 토큰이 다르면
            // → 이미 다른 요청이 refresh를 끝낸 것이다!
            if (!currentToken.isNullOrBlank() && currentToken != tokenUsedInRequest) {
                // 그냥 새 토큰으로 다시 요청만 보내면 됨 (refresh 다시 X)
                val newRequest = request.newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", "Bearer $currentToken")
                    .build()

                return chain.proceed(newRequest)
            }

            // 여기까지 왔다는 건:
            // - 아직 아무도 refresh 안 했거나
            // - 토큰이 여전히 같은 (진짜 만료된) 값이라는 뜻

            if (!isRefreshing) {
                // 내가 refresh 담당
                isRefreshing = true

                val refreshSuccess = runBlocking {
                    AuthManager.refreshAccessToken()
                }

                isRefreshing = false
                refreshLock.notifyAll()

                if (!refreshSuccess) {
                    Log.e(TAG, "토큰 갱신 실패")
                    // 실패하면 401로 끝냄
                    return chain.proceed(request)
                }
            } else {
                // 이미 다른 쓰레드가 refresh 중 → 끝날 때까지 대기
                while (isRefreshing) {
                    refreshLock.wait()
                }
            }

            // 5) 여기 오면 어쨌든 토큰은 새 걸로 바뀐 상태
            val newToken = AuthManager.getStoredAccessToken()
            if (newToken.isNullOrBlank()) {
                return chain.proceed(request)
            }

            val newRequest = request.newBuilder()
                .removeHeader("Authorization")
                .addHeader("Authorization", "Bearer $newToken")
                .build()

            return chain.proceed(newRequest)
        }
    }
}

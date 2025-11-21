package com.markoala.tomoandroid.ui.main

import android.content.Context
import android.util.Log
import com.markoala.tomoandroid.data.api.userApi
import com.markoala.tomoandroid.utils.auth.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun handleSignOut(context: Context, onComplete: () -> Unit) {
    Log.i("SignOut", "handleSignOut 함수 진입")
    val tokenManager = TokenManager(context)
    val accessTokenBefore = tokenManager.getAccessToken()
    val refreshTokenBefore = tokenManager.getRefreshToken()
    Log.i("SignOut", "로그아웃 요청 시작. accessToken: $accessTokenBefore, refreshToken: $refreshTokenBefore")
    CoroutineScope(Dispatchers.IO).launch {
        try {
            Log.i("SignOut", "로그아웃 API 요청 시작")
            val response = userApi.logout().execute()
            Log.i("SignOut", "서버 응답 코드: ${response.code()}, 메시지: ${response.message()}, 바디: ${response.body()}")
            Log.i("SignOut", "서버 응답 에러바디: ${response.errorBody()?.string()}")
            // 토큰 삭제
            tokenManager.clearTokens()
            val accessTokenAfter = tokenManager.getAccessToken()
            val refreshTokenAfter = tokenManager.getRefreshToken()
            Log.i("SignOut", "토큰 삭제 후 accessToken: $accessTokenAfter, refreshToken: $refreshTokenAfter")
            if (accessTokenAfter != null || refreshTokenAfter != null) {
                Log.w("SignOut", "토큰 삭제 실패: accessToken=$accessTokenAfter, refreshToken=$refreshTokenAfter")
            } else {
                Log.i("SignOut", "토큰이 정상적으로 삭제되었습니다.")
            }
            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {

                    Log.i("SignOut", "로그아웃 성공")
                } else {

                    Log.e("SignOut", "로그아웃 실패: ${response.errorBody()?.string()}" )
                }
                Log.i("SignOut", "onComplete 콜백 호출")
                onComplete()
            }
        } catch (e: Exception) {
            Log.e("SignOut", "네트워크 오류", e)
            CoroutineScope(Dispatchers.Main).launch {

                Log.i("SignOut", "onComplete 콜백 호출(예외)")
                onComplete()
            }
        }
    }
}

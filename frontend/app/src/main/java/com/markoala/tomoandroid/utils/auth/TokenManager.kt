package com.markoala.tomoandroid.utils.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TokenManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences( // sharedPreferences 로컷 저장소 생성
            "secure_prefs", // "secure_prefs"라는 이름으로 생성
            Context.MODE_PRIVATE // 앱 내부에서만 접근
        )

    companion object { // static과 같은개념
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit { // 확장 함수를 이용해 key-value 쌍으로 저장
            putString(ACCESS_TOKEN_KEY, accessToken)
            putString(REFRESH_TOKEN_KEY, refreshToken)
        }
    }

    fun getAccessToken(): String? { // 저장된 토큰 불러오기
        return sharedPreferences.getString(ACCESS_TOKEN_KEY, null)
    }

    fun getRefreshToken(): String? { // 저장된 토큰 불러오기
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, null)
    }

    fun clearTokens() { // 로그아웃 시 호출하여 저장된 모든 토큰 제거
        sharedPreferences.edit {
            remove(ACCESS_TOKEN_KEY)
            remove(REFRESH_TOKEN_KEY)
        }
    }

    fun hasValidTokens(): Boolean { // 토큰 유효성 검사 (두 토큰이 모두 존재하는지)
        return getAccessToken() != null && getRefreshToken() != null
    }
}

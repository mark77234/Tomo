package com.markoala.tomoandroid.auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.markoala.tomoandroid.data.api.apiService
import com.markoala.tomoandroid.utils.auth.TokenManager
import kotlinx.coroutines.tasks.await

object AuthManager { // 싱글톤 객체로 앱 전체에서 하나의 인스턴스만 사용
    private const val TAG = "AuthManager"
    lateinit var auth: FirebaseAuth
    private var tokenManager: TokenManager? = null

    fun init() { // 인스턴스 초기화
        auth = Firebase.auth
    }

    fun initTokenManager(context: Context) { // 토큰 매니저 초기화(Access, Refresh Token 저장소)
        tokenManager = TokenManager(context)
    }

    suspend fun firebaseAuthWithGoogle(
        idToken: String,
        context: Context,
        onResult: (Boolean, String?) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null) // 구글 ID 토큰
        try {
            val authResult = auth.signInWithCredential(credential).await() // 파베 인증
            if (authResult.user != null) {
                val user = auth.currentUser // 현재 인증된 사용자 정보
                user?.let {
                    // Firebase ID 토큰 가져오기
                    val firebaseToken = it.getIdToken(true).await()?.token // 파베 Id 토큰 발급
                    if (firebaseToken != null) {
                        Log.d(TAG, "Firebase ID Token acquired")
                        // 서버에서 access token과 refresh token 받아오기
                        val success = exchangeFirebaseTokenForServerTokens(
                            firebaseToken,
                            context
                        ) // Firebase IdToken -> 서버 Access, Refresh Token 교환
                        onResult(success, null)
                    } else {
                        Log.e(TAG, "Firebase ID 토큰 가져오기 실패")
                        onResult(false, "Firebase ID 토큰 가져오기 실패")
                    }
                }
            } else {
                onResult(false, "로그인 실패")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase 인증 실패", e)
            onResult(false, e.localizedMessage)
        }
    }

    private suspend fun exchangeFirebaseTokenForServerTokens( // Firebase IdToken -> 서버 Access, Refresh Token 교환
        firebaseToken: String,
        context: Context
    ): Boolean {
        return try {
            if (tokenManager == null) { // 토큰매니저가 null이면 초기화
                initTokenManager(context)
            }

            val response = apiService.getTokensWithFirebaseToken("Bearer $firebaseToken") // 서버에 요청

            if (response.isSuccessful) {
                // 헤더에서 토큰 추출
                val accessToken = response.headers()["Authorization"] // 응답 헤더의 Authorization 부분 추출
                val refreshToken = response.headers()["Refresh-Token"] // 응답 헤더의 refresh-token 부분 추출

                if (accessToken != null && refreshToken != null) {
                    // "Bearer " 제거
                    val cleanAccessToken = accessToken.removePrefix("Bearer ") // Bearer 부분 제거

                    // 토큰 저장
                    tokenManager?.saveTokens(
                        cleanAccessToken,
                        refreshToken
                    ) // access, refresh token 저장
                    Log.d(TAG, "토큰이 성공적으로 저장되었습니다")
                    true
                } else {
                    Log.e(TAG, "응답 헤더에서 토큰을 찾을 수 없습니다")
                    false
                }
            } else {
                Log.e(TAG, "서버 토큰 교환 실패: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "토큰 교환 중 오류 발생", e)
            false
        }
    }

    fun getStoredAccessToken(): String? {
        return tokenManager?.getAccessToken()
    }

    fun getStoredRefreshToken(): String? {
        return tokenManager?.getRefreshToken()
    }

    fun hasValidTokens(): Boolean {
        return tokenManager?.hasValidTokens() == true
    }

    suspend fun signOutSuspend(context: Context): Pair<Boolean, String?> {
        return try {
            // 저장된 토큰 삭제
            tokenManager?.clearTokens()

            if (::auth.isInitialized) auth.signOut()
            val credentialManager = CredentialManager.create(context)
            val clearRequest = ClearCredentialStateRequest()
            credentialManager.clearCredentialState(clearRequest)
            Pair(true, null)
        } catch (e: ClearCredentialException) {
            Log.w(TAG, "CredentialManager clear failed: ${e.localizedMessage}")
            Pair(true, e.localizedMessage)
        } catch (e: Exception) {
            Log.e(TAG, "SignOut failed", e)
            Pair(false, e.localizedMessage)
        }
    }
}

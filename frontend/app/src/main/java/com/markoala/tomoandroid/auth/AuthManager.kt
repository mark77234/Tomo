package com.markoala.tomoandroid.auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.markoala.tomoandroid.data.api.userApi
import com.markoala.tomoandroid.data.repository.AuthRepository
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
                        Log.d(TAG, "Firebase ID $firebaseToken")
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

            val response =
                userApi.getTokensWithFirebaseToken("Bearer $firebaseToken") // 서버에 요청

            if (response.isSuccessful) {
                // 헤더에서 토큰 추출

                val responseBody = response.body()

                val accessToken = responseBody?.data?.accessToken
                val refreshToken = responseBody?.data?.refreshToken

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
            } else if (response.code() == 401) {
                // 401 에러 발생 시 회원가입 진행
                Log.w(TAG, "401 에러 발생 - 회원가입을 진행합니다")

                // 현재 사용자 프로필 가져오기
                val userProfile = AuthRepository.getCurrentUserProfile()

                if (userProfile != null) {
                    try {
                        // 회원가입 진행
                        val signupResponse = AuthRepository.signUp(userProfile)

                        if (signupResponse.isSuccessful) {
                            Log.d(TAG, "회원가입 성공 - 다시 토큰 교환을 시도합니다")

                            // 회원가입 성공 후 다시 토큰 교환 시도
                            val retryResponse =
                                userApi.getTokensWithFirebaseToken("Bearer $firebaseToken")

                            if (retryResponse.isSuccessful) {
                                val responseBody = retryResponse.body()
                                val accessToken = responseBody?.data?.accessToken
                                val refreshToken = responseBody?.data?.refreshToken

                                if (accessToken != null && refreshToken != null) {
                                    val cleanAccessToken = accessToken.removePrefix("Bearer ")
                                    tokenManager?.saveTokens(cleanAccessToken, refreshToken)
                                    Log.d(TAG, "회원가입 후 토큰이 성공적으로 저장되었습니다")
                                    true
                                } else {
                                    Log.e(TAG, "회원가입 후 응답 헤더에서 토큰을 찾을 수 없습니다")
                                    false
                                }
                            } else {
                                Log.e(TAG, "회원가입 후 토큰 교환 실패: ${retryResponse.code()}")
                                false
                            }
                        } else {
                            Log.e(TAG, "회원가입 실패: ${signupResponse.code()}")
                            false
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "회원가입 중 오류 발생", e)
                        false
                    }
                } else {
                    Log.e(TAG, "사용자 프로필을 가져올 수 없어 회원가입을 진행할 수 없습니다")
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

    // 토큰 갱신 메서드
    suspend fun refreshAccessToken(): Boolean {
        return try {
            val refreshToken = tokenManager?.getRefreshToken()
            if (refreshToken == null) {
                Log.e(TAG, "Refresh Token이 없습니다")
                return false
            }

            val response = userApi.refreshToken(refreshToken)

            if (response.isSuccessful) {
                val responseBody = response.body()
                val newAccessToken = responseBody?.data?.accessToken
                val newRefreshToken = responseBody?.data?.refreshToken

                if (newAccessToken != null && newRefreshToken != null) {
                    val cleanAccess = newAccessToken.removePrefix("Bearer ")
                    tokenManager?.saveTokens(cleanAccess, newRefreshToken)

                    Log.d(TAG, "토큰 갱신 성공")
                    true
                } else {
                    Log.e(TAG, "토큰 갱신 응답에서 토큰을 찾을 수 없습니다")
                    false
                }
            } else {
                Log.e(TAG, "토큰 갱신 실패: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "토큰 갱신 중 오류 발생", e)
            false
        }
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

    // 401 에러 발생 시 자동 로그아웃을 위한 콜백
    private var onUnauthorizedCallback: (() -> Unit)? = null

    fun setUnauthorizedCallback(callback: () -> Unit) {
        onUnauthorizedCallback = callback
    }

    suspend fun handleUnauthorized(context: Context) {
        Log.w(TAG, "401 Unauthorized - 자동 로그아웃 처리")
        signOutSuspend(context)
        onUnauthorizedCallback?.invoke()
    }

    /**
     * 재인증을 수행합니다 (Google 로그인)
     */
    private suspend fun reauthenticateUser(context: Context): Boolean {
        return try {
            val firebaseUser = auth.currentUser ?: return false

            Log.d(TAG, "재인증 시작")

            // Google ID 옵션 설정 (web client ID는 실제 값으로 교체 필요)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(
                    context.getString(
                        context.resources.getIdentifier(
                            "default_web_client_id",
                            "string",
                            context.packageName
                        )
                    )
                )
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken

            // Firebase 재인증
            val authCredential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseUser.reauthenticate(authCredential).await()

            Log.d(TAG, "재인증 성공")
            true
        } catch (e: Exception) {
            Log.e(TAG, "재인증 실패", e)
            false
        }
    }

    /**
     * 계정 삭제: 서버, Firestore, Firebase Authentication에서 계정 삭제
     */
    suspend fun deleteAccount(context: Context): Pair<Boolean, String?> {
        return try {
            val firebaseUser = auth.currentUser
            val uuid = firebaseUser?.uid

            if (uuid == null) {
                Log.e(TAG, "사용자 정보를 찾을 수 없습니다")
                return Pair(false, "사용자 정보를 찾을 수 없습니다")
            }

            // 1. 서버에서 계정 삭제
            try {
                val serverResponse = AuthRepository.deleteUserFromServer()
                if (serverResponse.isSuccessful) {
                    Log.d(TAG, "서버 계정 삭제 성공")
                } else if (serverResponse.code() == 404) {
                    // 서버에 사용자가 없으면 이미 삭제된 것으로 간주
                    Log.w(TAG, "서버에 사용자가 없습니다 (이미 삭제됨). 계속 진행합니다.")
                } else {
                    Log.e(TAG, "서버 계정 삭제 실패: ${serverResponse.code()}")
                    // 서버 삭제 실패해도 계속 진행 (이미 삭제되었을 수 있음)
                }
            } catch (e: Exception) {
                Log.e(TAG, "서버 계정 삭제 중 오류 발생", e)
                // 서버 삭제 실패해도 계속 진행 (이미 삭제되었을 수 있음)
                Log.w(TAG, "서버 삭제 오류 무시하고 계속 진행")
            }

            // 2. Firestore에서 사용자 문서 삭제
            try {
                val firestoreDeleted = AuthRepository.deleteUserFromFirestore(uuid)
                if (firestoreDeleted) {
                    Log.d(TAG, "Firestore 사용자 문서 삭제 성공")
                } else {
                    Log.w(TAG, "Firestore 사용자 문서 삭제 실패")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Firestore 삭제 중 오류 발생", e)
                // Firestore 삭제 실패해도 계속 진행
            }

            // 3. 재인증 시도
            Log.d(TAG, "Firebase 계정 삭제를 위해 재인증 시도")
            val reauthSuccess = reauthenticateUser(context)

            if (!reauthSuccess) {
                Log.w(TAG, "재인증 실패. 사용자에게 다시 로그인하도록 안내합니다.")
                return Pair(false, "계정 삭제를 위해 다시 로그인해주세요.")
            }

            // 4. Firebase Authentication에서 계정 삭제
            try {
                val firebaseDeleted = AuthRepository.deleteFirebaseAccount()
                if (firebaseDeleted) {
                    Log.d(TAG, "Firebase 계정 삭제 성공")
                } else {
                    Log.e(TAG, "Firebase 계정 삭제 실패")
                    return Pair(false, "Firebase 계정 삭제 실패")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Firebase 계정 삭제 중 오류 발생", e)
                return Pair(false, "Firebase 계정 삭제 실패: ${e.localizedMessage}")
            }

            // 5. 로컬 토큰 삭제 및 로그아웃 처리
            tokenManager?.clearTokens()
            val credentialManager = CredentialManager.create(context)
            val clearRequest = ClearCredentialStateRequest()
            try {
                credentialManager.clearCredentialState(clearRequest)
            } catch (e: ClearCredentialException) {
                Log.w(TAG, "CredentialManager clear failed: ${e.localizedMessage}")
            }

            Log.d(TAG, "계정 삭제 완료")
            Pair(true, null)
        } catch (e: Exception) {
            Log.e(TAG, "계정 삭제 중 오류 발생", e)
            Pair(false, e.localizedMessage)
        }
    }


}

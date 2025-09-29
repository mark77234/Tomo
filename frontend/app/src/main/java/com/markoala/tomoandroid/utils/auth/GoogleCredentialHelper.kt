package com.markoala.tomoandroid.utils.auth

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.markoala.tomoandroid.R

object GoogleCredentialHelper { // Singleton 객체로 변경
    /**
     * CredentialManager로부터 Google ID Token을 가져온다 (suspend).
     * 예외는 호출자에게 전달됨.
     */
    suspend fun fetchGoogleIdToken( // suspend 함수(코루틴 내에서 호출 가능, 네트워크 작업을 비동기적 처리)
        activity: Activity, // 현재 Activity
        context: Context, // 앱 Context(리소스 접근용)
        credentialManager: CredentialManager // Android Credentials API
    ): String { // 반환값: 문자열
        val googleIdOption = GetGoogleIdOption.Builder() // Google Id Token 요청 옵션
            .setServerClientId(context.getString(R.string.default_web_client_id)) // 서버 클라이언트 ID 설정
            .setFilterByAuthorizedAccounts(false) // 이미 인증된 계정만 가져오지 않음
            .setAutoSelectEnabled(false) // 사용자 선택 없이 자동으로 계정 선택 X
            .build()

        val request = GetCredentialRequest.Builder() // Credential 요청
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential( // Credential 가져오기
            context = activity,
            request = request
        )

        val credential = result.credential // credential만 추출
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL // Google Id Token 타입 체크
        ) {
            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(credential.data) // 파싱 idToken만 추출
            val idToken = googleIdTokenCredential.idToken // ID 토큰
            if (idToken.isNotEmpty()) {
                return idToken // 반환
            } else {
                throw Exception("ID 토큰이 비어있습니다.")
            }
        } else {
            throw Exception("올바른 자격증명이 반환되지 않았습니다.")
        }
    }
}

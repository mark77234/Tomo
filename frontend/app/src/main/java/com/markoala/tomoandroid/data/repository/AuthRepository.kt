package com.markoala.tomoandroid.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.data.api.apiService
import com.markoala.tomoandroid.data.model.PostExampleResponse
import com.markoala.tomoandroid.data.model.UserData
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object AuthRepository {
    /**
     * AuthManager.firebaseAuthWithGoogle 콜백을 suspend로 래핑해서 UserData 반환
     */
    suspend fun firebaseSignIn(idToken: String): UserData = suspendCancellableCoroutine { cont ->
        try {
            AuthManager.firebaseAuthWithGoogle(idToken) { success, err ->
                if (success) {
                    val firebaseUser = FirebaseAuth.getInstance().currentUser
                    if (firebaseUser != null) {
                        val userData = UserData(
                            uuid = firebaseUser.uid,
                            email = firebaseUser.email ?: "",
                            username = firebaseUser.displayName ?: ""
                        )
                        cont.resume(userData)
                    } else {
                        cont.resumeWithException(Exception("Firebase 사용자 정보가 비어있습니다."))
                    }
                } else {
                    cont.resumeWithException(Exception(err ?: "Firebase 인증 실패"))
                }
            }
        } catch (e: Exception) {
            cont.resumeWithException(e)
        }
    }

    /**
     * 서버에 사용자 정보 전송 (Retrofit 동기 호출을 IO 스레드에서 수행)
     */
    suspend fun signUp(userData: UserData): Response<PostExampleResponse> =
        withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                apiService.signup(userData).execute()
            } catch (e: Exception) {
                throw e
            }
        }
}

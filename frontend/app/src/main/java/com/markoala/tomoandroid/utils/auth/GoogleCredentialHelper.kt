package com.markoala.tomoandroid.utils.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.markoala.tomoandroid.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

sealed class GoogleIdTokenResult {
    data class Success(val idToken: String) : GoogleIdTokenResult()
    object Cancelled : GoogleIdTokenResult()
    data class Failure(val message: String) : GoogleIdTokenResult()
}

object GoogleCredentialHelper {
    private const val TAG = "GoogleCredentialHelper"

    private fun googleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.DEFAULT_WEB_CLIENT_ID)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    /**
     * signOut을 먼저 수행해 항상 계정 선택 UI가 뜨도록 한 뒤 Sign-In Intent를 반환한다.
     */
    suspend fun prepareSignInIntent(context: Context): Intent {
        val client = googleSignInClient(context)
        suspendCancellableCoroutine<Unit> { cont ->
            client.signOut().addOnCompleteListener {
                cont.resume(Unit)
            }.addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
        }
        return client.signInIntent
    }

    fun extractIdTokenFromIntent(data: Intent?): GoogleIdTokenResult {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken.isNullOrBlank()) {
                GoogleIdTokenResult.Failure("ID 토큰이 비어있습니다")
            } else {
                GoogleIdTokenResult.Success(idToken)
            }
        } catch (e: ApiException) {
            if (e.statusCode == CommonStatusCodes.CANCELED) {
                GoogleIdTokenResult.Cancelled
            } else {
                Log.e(TAG, "GoogleSignIn ApiException", e)
                GoogleIdTokenResult.Failure(e.localizedMessage ?: "Google 로그인에 실패했습니다")
            }
        } catch (e: Exception) {
            Log.e(TAG, "GoogleSignIn 실패", e)
            GoogleIdTokenResult.Failure(e.localizedMessage ?: "Google 로그인에 실패했습니다")
        }
    }
}

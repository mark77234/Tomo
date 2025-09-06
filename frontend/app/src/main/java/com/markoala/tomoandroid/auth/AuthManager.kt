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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AuthManager {
    private const val TAG = "AuthManager"
    lateinit var auth: FirebaseAuth

    // 반드시 Application 또는 Activity Context로 한 번 초기화하세요.
    fun init(context: Context) {
        auth = Firebase.auth
    }

    // Google idToken으로 Firebase sign-in
    fun firebaseAuthWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.localizedMessage)
                }
            }
    }

    /**
     * sign out + credential manager 상태 초기화(권장)
     *
     * 내부에서 코루틴을 실행해 suspend 함수를 호출합니다.
     * onComplete는 credential 상태 초기화가 끝난 뒤 호출됩니다.
     */
    fun signOut(context: Context, onComplete: (Boolean, String?) -> Unit) {
        try {
            if (::auth.isInitialized) auth.signOut()

            // credential 상태 지우는 suspend 함수는 코루틴에서 호출해야 함
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val credentialManager = CredentialManager.create(context)
                    val clearRequest = ClearCredentialStateRequest()
                    credentialManager.clearCredentialState(clearRequest) // suspend 호출
                    // 성공적으로 지워졌거나 에러가 발생해도 Firebase 측은 이미 signOut 됐으므로 true로 처리 가능
                    onComplete(true, null)
                } catch (e: ClearCredentialException) {
                    Log.w(TAG, "CredentialManager clear failed: ${e.localizedMessage}")
                    // ClearCredential 실패는 치명적이지 않으므로 성공 콜백으로 처리할 수도 있음
                    onComplete(true, e.localizedMessage)
                } catch (e: Exception) {
                    Log.e(TAG, "Error clearing credential state", e)
                    onComplete(false, e.localizedMessage)
                }
            }
        } catch (e: Exception) {
            onComplete(false, e.localizedMessage)
        }
    }
}

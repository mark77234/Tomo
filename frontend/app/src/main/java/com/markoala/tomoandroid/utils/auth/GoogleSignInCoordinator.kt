package com.markoala.tomoandroid.utils.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.data.repository.AuthRepository
import com.markoala.tomoandroid.data.repository.UserRepository
import com.markoala.tomoandroid.ui.components.ToastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object GoogleSignInCoordinator {
    private const val TAG = "GoogleSignInCoordinator"

    suspend fun startGoogleSignIn(
        context: Context,
        toastManager: ToastManager,
        setLoading: (Boolean) -> Unit,
        launchIntent: (Intent) -> Unit
    ) {
        setLoading(true)
        try {
            val intent = GoogleCredentialHelper.prepareSignInIntent(context)
            launchIntent(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In Intent 준비 실패", e)
            toastManager.showError(e.localizedMessage ?: "Google 로그인 준비에 실패했습니다")
            setLoading(false)
        }
    }

    fun handleGoogleSignInResult(
        data: Intent?,
        context: Context,
        toastManager: ToastManager,
        coroutineScope: CoroutineScope,
        setLoading: (Boolean) -> Unit,
        onSignedIn: () -> Unit
    ) {
        when (val tokenResult = GoogleCredentialHelper.extractIdTokenFromIntent(data)) {
            GoogleIdTokenResult.Cancelled -> {
                toastManager.showWarning("로그인이 취소되었습니다")
                setLoading(false)
            }

            is GoogleIdTokenResult.Failure -> {
                toastManager.showError(tokenResult.message)
                setLoading(false)
            }

            is GoogleIdTokenResult.Success -> {
                coroutineScope.launch {
                    proceedWithFirebase(
                        idToken = tokenResult.idToken,
                        context = context,
                        toastManager = toastManager,
                        coroutineScope = coroutineScope,
                        setLoading = setLoading,
                        onSignedIn = onSignedIn
                    )
                }
            }
        }
    }

    private suspend fun proceedWithFirebase(
        idToken: String,
        context: Context,
        toastManager: ToastManager,
        coroutineScope: CoroutineScope,
        setLoading: (Boolean) -> Unit,
        onSignedIn: () -> Unit
    ) {
        AuthManager.firebaseAuthWithGoogle(idToken, context) { success, error ->
            if (success) {
                coroutineScope.launch {
                    try {
                        val userProfile = AuthRepository.getCurrentUserProfile()
                        if (userProfile == null) {
                            toastManager.showError("사용자 프로필을 가져올 수 없습니다")
                            return@launch
                        }

                        val exists = AuthRepository.checkUserExists(userProfile.uuid)
                        if (!exists) {
                            AuthRepository.signUp(userProfile)
                            UserRepository.saveUserToFirestore(userProfile)
                            toastManager.showSuccess("회원가입 성공")
                        } else {
                            toastManager.showSuccess("로그인 성공")
                        }
                        onSignedIn()
                    } catch (e: Exception) {
                        Log.e(TAG, "사용자 프로필 처리 실패: ${e.message}", e)
                        toastManager.showError("사용자 프로필 처리 실패: ${e.message}")
                    } finally {
                        setLoading(false)
                    }
                }
            } else {
                Log.e(TAG, "Firebase 인증 또는 토큰 교환 실패: $error")
                val errorMessage = when (error) {
                    "TOKEN_EXPIRED" -> "Google 로그인 세션이 만료되었습니다. 다시 시도해주세요."
                    else -> "로그인 실패: $error"
                }
                toastManager.showError(errorMessage)
                setLoading(false)
            }
        }
    }
}

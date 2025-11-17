package com.markoala.tomoandroid.utils.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.auth.AuthManager.FirebaseSignInResult
import com.markoala.tomoandroid.auth.AuthManager.ServerLoginResult
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
                    handleFirebaseAndServerLogin(
                        googleIdToken = tokenResult.idToken,
                        context = context,
                        toastManager = toastManager,
                        setLoading = setLoading,
                        onSignedIn = onSignedIn
                    )
                }
            }
        }
    }

    private suspend fun handleFirebaseAndServerLogin(
        googleIdToken: String,
        context: Context,
        toastManager: ToastManager,
        setLoading: (Boolean) -> Unit,
        onSignedIn: () -> Unit
    ) {
        try {
            when (val firebaseResult = AuthManager.signInWithGoogleIdToken(googleIdToken)) {
                is FirebaseSignInResult.Success -> {
                    val firebaseToken = firebaseResult.firebaseIdToken
                    when (val serverResult = AuthManager.loginWithFirebaseToken(firebaseToken, context)) {
                        ServerLoginResult.Success -> {
                            toastManager.showSuccess("로그인 성공")
                            onSignedIn()
                        }

                        ServerLoginResult.NeedsSignup -> {
                            handleServerSignupAndRetry(
                                firebaseToken = firebaseToken,
                                context = context,
                                toastManager = toastManager,
                                onSignedIn = onSignedIn
                            )
                        }

                        is ServerLoginResult.Failure -> {
                            toastManager.showError(serverResult.message ?: "서버 로그인에 실패했습니다")
                        }
                    }
                }

                FirebaseSignInResult.CredentialExpired -> {
                    toastManager.showError("Google 로그인 세션이 만료되었습니다. 다시 시도해주세요.")
                }

                is FirebaseSignInResult.Failure -> {
                    toastManager.showError(firebaseResult.message ?: "Firebase 로그인에 실패했습니다")
                }
            }
        } finally {
            setLoading(false)
        }
    }

    private suspend fun handleServerSignupAndRetry(
        firebaseToken: String,
        context: Context,
        toastManager: ToastManager,
        onSignedIn: () -> Unit
    ) {
        val userProfile = AuthRepository.getCurrentUserProfile()
        if (userProfile == null) {
            toastManager.showError("사용자 프로필을 가져올 수 없습니다")
            return
        }

        try {
            val signupResponse = AuthRepository.signUp(userProfile)
            if (signupResponse.isSuccessful) {
                UserRepository.saveUserToFirestore(userProfile)
                val retryResult = AuthManager.loginWithFirebaseToken(firebaseToken, context)
                if (retryResult is ServerLoginResult.Success) {
                    toastManager.showSuccess("회원가입 성공")
                    onSignedIn()
                } else if (retryResult is ServerLoginResult.Failure) {
                    toastManager.showError(retryResult.message ?: "회원가입 후 로그인에 실패했습니다")
                }
            } else {
                toastManager.showError("회원가입에 실패했습니다: ${signupResponse.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "회원가입 처리 실패", e)
            toastManager.showError("회원가입 처리 실패: ${e.localizedMessage}")
        }
    }
}

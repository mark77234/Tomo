package com.markoala.tomoandroid.utils.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.auth.AuthManager.FirebaseSignInResult
import com.markoala.tomoandroid.auth.AuthManager.ServerLoginResult
import com.markoala.tomoandroid.data.model.user.UserProfile
import com.markoala.tomoandroid.data.repository.AuthRepository
import com.markoala.tomoandroid.data.repository.UserRepository
import com.markoala.tomoandroid.ui.components.ToastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
                    val userProfile = AuthRepository.getCurrentUserProfile()
                    when (val serverResult = AuthManager.loginWithFirebaseToken(firebaseToken, context)) {
                        ServerLoginResult.Success -> {
                            userProfile?.let { syncUserProfileWithFcmToken(it) }
                            toastManager.showSuccess("로그인 성공")
                            onSignedIn()
                        }

                        ServerLoginResult.NeedsSignup -> {
                            handleServerSignupAndRetry(
                                firebaseToken = firebaseToken,
                                userProfile = userProfile,
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
        userProfile: UserProfile?,
        context: Context,
        toastManager: ToastManager,
        onSignedIn: () -> Unit
    ) {
        val profile = userProfile ?: AuthRepository.getCurrentUserProfile()
        if (profile == null) {
            toastManager.showError("사용자 프로필을 가져올 수 없습니다")
            return
        }

        try {
            val signupResponse = AuthRepository.signUp(profile)
            if (signupResponse.isSuccessful) {
                syncUserProfileWithFcmToken(profile)
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

    private suspend fun syncUserProfileWithFcmToken(userProfile: UserProfile) {
        try {
            val fcmToken = try {
                FirebaseMessaging.getInstance().token.await()
            } catch (e: Exception) {
                Log.w(TAG, "FCM 토큰 가져오기 실패", e)
                null
            }

            UserRepository.saveUserToFirestore(userProfile, fcmToken)
            if (fcmToken != null) {
                Log.d(TAG, "FCM 토큰 Firestore 저장 완료")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Firestore에 사용자/FCM 토큰 저장 실패", e)
        }
    }
}

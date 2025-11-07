package com.markoala.tomoandroid.ui.login.components

import android.accounts.AccountManager
import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.data.repository.AuthRepository
import com.markoala.tomoandroid.data.repository.UserRepository
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.utils.auth.GoogleCredentialHelper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun GoogleSignUpButton(onSignedIn: () -> Unit) {
    val context = LocalContext.current
    val toastManager = LocalToastManager.current
    val activity = context as? ComponentActivity ?: (context as? Activity)
    if (activity == null) {
        Text("Activity 컨텍스트를 찾을 수 없습니다.")
        return
    }

    val credentialManager = remember { CredentialManager.create(context) }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    CustomButton(
        text = if (isLoading) "연결 중..." else "Google 계정으로 로그인",
        onClick = {
            coroutineScope.launch {
                try {
                    isLoading = true
                    val idToken = try {
                        GoogleCredentialHelper.fetchGoogleIdToken(
                            activity = activity,
                            context = context,
                            credentialManager = credentialManager
                        )
                    } catch (e: Exception) {
                        if (e.message?.contains("No credentials available") == true) {
                            val accountManager = AccountManager.get(activity)
                            accountManager.addAccount(
                                "com.google",
                                null,
                                null,
                                null,
                                activity,
                                { _ ->
                                    activity.runOnUiThread {
                                        toastManager.showSuccess("계정이 추가되었습니다.")
                                    }
                                },
                                null
                            )
                        }
                        throw e
                    }

                    AuthManager.firebaseAuthWithGoogle(idToken, context) { success, error ->
                        if (success) {
                            coroutineScope.launch {
                                try {
                                    val userProfile = AuthRepository.getCurrentUserProfile()
                                    if (userProfile != null) {
                                        val exists = AuthRepository.checkUserExists(userProfile.uuid)
                                        if (!exists) {
                                            AuthRepository.signUp(userProfile)
                                            UserRepository.saveUserToFirestore(userProfile)
                                            toastManager.showSuccess("회원가입 성공")
                                        } else {
                                            toastManager.showSuccess("로그인 성공")
                                        }
                                        onSignedIn()
                                    } else {
                                        toastManager.showError("사용자 프로필을 가져올 수 없습니다")
                                    }
                                } catch (e: Exception) {
                                    Log.e("GoogleSignIn", "사용자 프로필 처리 실패: ${e.message}", e)
                                    toastManager.showError("사용자 프로필 처리 실패: ${e.message}")
                                } finally {
                                    isLoading = false
                                }
                            }
                        } else {
                            Log.e("GoogleSignIn", "Firebase 인증 또는 토큰 교환 실패: $error")
                            toastManager.showError("로그인 실패: $error")
                            isLoading = false
                        }
                    }
                } catch (e: CancellationException) {
                    toastManager.showError("작업 취소됨: ${e.message}")
                    Log.w("GoogleSignIn", "작업 취소됨 ${e.message}")
                    isLoading = false
                } catch (e: Exception) {
                    Log.e("GoogleSignIn", "로그인 실패: ${e.message}", e)
                    toastManager.showError("로그인 실패: ${e.message}")
                    isLoading = false
                }
            }
        },
        enabled = !isLoading,
        style = ButtonStyle.Primary,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "Google Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
        }
    )
}

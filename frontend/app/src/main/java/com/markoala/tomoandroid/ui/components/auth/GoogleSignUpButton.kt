package com.markoala.tomoandroid.ui.components.auth

import android.accounts.AccountManager
import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.utils.auth.GoogleCredentialHelper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun GoogleSignUpButton(onSignedIn: () -> Unit) {
    val context = LocalContext.current
    val toastManager = LocalToastManager.current
    val activity = context as? ComponentActivity ?: (context as? Activity)
    if (activity == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Activity 컨텍스트를 찾을 수 없습니다.")
        }
        return
    }

    val credentialManager = remember { CredentialManager.create(context) } // Google 계정 토큰 획득
    val coroutineScope = rememberCoroutineScope() // 버튼 클릭 시 비동기 처리용

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = CustomColor.gray100,
                shape = RoundedCornerShape(16.dp)
            ),
        onClick = {
            coroutineScope.launch {
                try {
                    // 1) Credential에서 ID 토큰 가져오기
                    val idToken = try {
                        GoogleCredentialHelper.fetchGoogleIdToken(
                            activity = activity,
                            context = context,
                            credentialManager = credentialManager
                        )
                    } catch (e: Exception) {
                        // No credentials -> 계정 추가 유도
                        if (e.message?.contains("No credentials available") == true) {
                            val accountManager = AccountManager.get(activity)
                            accountManager.addAccount(
                                "com.google",
                                null,
                                null,
                                null,
                                activity,
                                { future ->
                                    activity.runOnUiThread {
                                        toastManager.showSuccess("계정이 추가되었습니다.")
                                    }
                                },
                                null
                            )
                        }
                        throw e
                    }

                    // 2) Firebase 인증 및 서버 토큰 교환
                    AuthManager.firebaseAuthWithGoogle(idToken, context) { success, error ->
                        if (success) {
                            coroutineScope.launch {
                                try {
                                    // 3) UserData 생성 및 회원가입/로그인 처리
                                    val userData = AuthRepository.getCurrentUserData()

                                    if (userData != null) {
                                        val exists = AuthRepository.checkUserExists(userData.uuid)
                                        Log.w("GoogleSignIn", "uuid: ${userData.uuid}")
                                        if (!exists) {
                                            AuthRepository.signUp(userData)
                                            UserRepository.saveUserToFirestore(userData) // 최초 가입 시에만 저장
                                            toastManager.showSuccess("회원가입 성공")
                                        } else {
                                            toastManager.showSuccess("로그인 성공")
                                        }

                                        // 성공 콜백
                                        onSignedIn()
                                    } else {
                                        Log.e("GoogleSignIn", "사용자 데이터를 가져올 수 없습니다")
                                        toastManager.showError("사용자 데이터를 가져올 수 없습니다")
                                    }
                                } catch (e: Exception) {
                                    Log.e("GoogleSignIn", "사용자 데이터 처리 실패: ${e.message}", e)
                                    toastManager.showError("사용자 데이터 처리 실패: ${e.message}")
                                }
                            }
                        } else {
                            Log.e("GoogleSignIn", "Firebase 인증 또는 토큰 교환 실패: $error")
                            toastManager.showError("로그인 실패: $error")
                        }
                    }

                } catch (e: CancellationException) {
                    toastManager.showError("작업 취소됨: ${e.message}")
                    Log.w("GoogleSignIn", "작업 취소됨 ${e.message}")
                } catch (e: Exception) {
                    Log.e("GoogleSignIn", "로그인 실패: ${e.message}", e)
                    toastManager.showError("로그인 실패: ${e.message}")
                }
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "Google Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            CustomText(
                text = "Google 계정으로 로그인",
                color = Color.Black,
                type = CustomTextType.labelLarge
            )
        }
    }
}

package com.markoala.tomoandroid.ui.components.auth

import android.accounts.AccountManager
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.markoala.tomoandroid.data.model.UserData
import com.markoala.tomoandroid.data.repository.AuthRepository
import com.markoala.tomoandroid.data.repository.UserRepository
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.utils.auth.GoogleCredentialHelper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GoogleSignUpButton(onSignedIn: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity ?: (context as? Activity)
    if (activity == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Activity 컨텍스트를 찾을 수 없습니다.")
        }
        return
    }

    val credentialManager = remember { CredentialManager.create(context) } // Google 계정 토큰 획득
    val coroutineScope = rememberCoroutineScope() // 버튼 클릭 시 비동기 처리용

    Column {
        Button(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = CustomColor.lightGray,
                    shape = RoundedCornerShape(8.dp)
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
                                            Toast.makeText(
                                                activity,
                                                "계정 추가 화면이 열렸습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    null
                                )
                            }
                            throw e
                        }

                        // 2) Firebase 인증 & UserData 획득
                        val userData: UserData = AuthRepository.firebaseSignIn(idToken)

                        // 3) 서버로 전송 (optional) — 오류 있어도 흐름 유지 가능
                        try {
                            val resp = AuthRepository.signUp(userData)
                            if (resp.isSuccessful) {
                                withContext(kotlinx.coroutines.Dispatchers.Main) {
                                    Toast.makeText(
                                        activity,
                                        "회원가입 성공 (${resp.code()})",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val err = resp.errorBody()?.string()
                                Log.e("GoogleSignInButton", "서버 응답 실패: ${resp.code()} / $err")
                            }
                        } catch (e: Exception) {
                            Log.e("GoogleSignInButton", "서버 전송 예외 ${e.message}", e)
                        }

                        // 4) Firestore 저장 ->  로그인 할때마다 저장하므로 수정 필요
                        try {
                            UserRepository.saveUserToFirestore(userData)
                        } catch (e: Exception) {
                            Log.e("GoogleSignInButton", "Firestore 저장 실패 ${e.message}", e)
                        }

                        // 성공 콜백
                        onSignedIn()

                    } catch (e: CancellationException) {
                        // 취소 무시
                        Log.w("GoogleSignInButton", "작업 취소됨 ${e.message}")
                    } catch (e: Exception) {
                        Log.e("GoogleSignInButton", "로그인 실패: ${e.message}")

                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = "Google Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Google 계정으로 로그인", color = Color.Black)
            }
        }
    }
}

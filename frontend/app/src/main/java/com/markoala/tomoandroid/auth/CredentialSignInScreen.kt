// 파일: CredentialSignInScreen.kt
package com.markoala.tomoandroid.auth

import android.accounts.AccountManager
import android.app.Activity
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
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.markoala.tomoandroid.ui.theme.CustomColor
import kotlinx.coroutines.launch

@Composable
fun CredentialSignInScreen(onSignedIn: () -> Unit) {
    val context = LocalContext.current
    // Compose에서 Activity 필요(credentialManager.getCredential에 activity 기반 context 권장)
    val activity = context as? ComponentActivity ?: (context as? Activity)
    if (activity == null) {
        // 안전 장치: ComponentActivity가 아니면 아무 동작 못함
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Activity 컨텍스트를 찾을 수 없습니다.")
        }
        return
    }

    // CredentialManager 인스턴스
    val credentialManager = remember { CredentialManager.create(context) }
    val coroutineScope = rememberCoroutineScope()
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }

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
                        // 1) Google ID option 구성 (서버용 web client id 사용)
                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setServerClientId(context.getString(com.markoala.tomoandroid.R.string.default_web_client_id))
                            // 이미 허용된 계정만 표시하려면 true (원하면 false)
                            .setFilterByAuthorizedAccounts(false)
                            .setAutoSelectEnabled(false) // 자동 선택 방지
                            .build()

                        // 2) 요청 빌드
                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        // 3) Credential Manager 호출 (activity 기반 context 사용 권장)
                        val result = credentialManager.getCredential(
                            context = activity,
                            request = request
                        )

                        // 4) 반환된 credential 처리
                        val credential = result.credential
                        if (credential is CustomCredential &&
                            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                        ) {
                            // GoogleIdTokenCredential로 변환
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)
                            val idToken = googleIdTokenCredential.idToken

                            if (idToken.isNotEmpty()) {
                                // 기존 Firebase 인증 함수 사용
                                AuthManager.firebaseAuthWithGoogle(idToken) { success, err ->
                                    if (success) {
                                        // Firestore에 사용자 정보 저장
                                        val user = firebaseAuth.currentUser
                                        user?.let {
                                            val userData = hashMapOf(
                                                "uid" to it.uid,
                                                "name" to (it.displayName ?: ""),
                                                "email" to (it.email ?: ""),
                                            )
                                            firestore.collection("users").document(it.uid)
                                                .set(userData)
                                        }
                                        onSignedIn()
                                    } else {
                                        Toast.makeText(
                                            activity,
                                            "로그인에 실패하였습니다. $err",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("CredentialSignIn", "로그인 중 예외 발생", e)
                        if (e is GetCredentialException && e.errorMessage?.contains("No credentials available") == true) {
                            val accountManager = AccountManager.get(activity)
                            accountManager.addAccount(
                                "com.google", // Google 계정 타입
                                null, // 인증 토큰 타입
                                null, // 필수 권한
                                null, // 추가 옵션
                                activity, // Activity
                                { future ->
                                    // 계정 추가 후 콜백 (필요시 처리)
                                    // 계정 추가가 완료되면 onSignedIn() 호출
                                    onSignedIn()
                                },
                                null // Handler
                            )
                        }

                    }
                }
            },
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color.White
            )

        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = com.markoala.tomoandroid.R.drawable.ic_google_logo),
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

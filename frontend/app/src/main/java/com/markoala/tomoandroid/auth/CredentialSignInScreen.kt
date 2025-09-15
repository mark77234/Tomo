// 파일: CredentialSignInScreen.kt
package com.markoala.tomoandroid.auth

import android.accounts.AccountManager
import android.app.Activity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import kotlin.text.contains

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {

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
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        if (idToken.isNotEmpty()) {
                            // 기존 Firebase 인증 함수 사용
                            AuthManager.firebaseAuthWithGoogle(idToken) { success, err ->
                                if (success) {
                                    onSignedIn()
                                } else {
                                    Toast.makeText(activity, "로그인에 실패하였습니다. $err", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }  catch (e: Exception) {
                    if( e is GetCredentialException && e.errorMessage?.contains("No credentials available") == true){
                        val accountManager = AccountManager.get(activity)
                        accountManager.addAccount(
                            "com.google", // Google 계정 타입
                            null, // 인증 토큰 타입
                            null, // 필수 권한
                            null, // 추가 옵션
                            activity, // Activity
                            { future ->
                                // 계정 추가 후 콜백 (필요시 처리)
                                // 보통은 계정 추가 후 사용자가 다시 버튼을 누르게 안내
                            },
                            null // Handler
                        )
                    }

                    android.util.Log.e("CredentialSignIn", "알 수 없는 오류", e)
                    Toast.makeText(activity, "알 수 없는 오류: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }) {
            Text(text = "구글로그인")
        }
    }
}

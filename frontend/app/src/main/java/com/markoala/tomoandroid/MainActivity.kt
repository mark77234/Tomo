package com.markoala.tomoandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.auth.SignInScreen
import com.markoala.tomoandroid.ui.UserProfileScreen
import com.markoala.tomoandroid.ui.theme.TomoAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // AuthManager 초기화 (Application context 권장)
        AuthManager.init(applicationContext)

        setContent {
            TomoAndroidTheme {
                var isSignedIn by remember { mutableStateOf(false) }

                // Compose 시작 시 현재 로그인 상태 확인
                LaunchedEffect(Unit) {
                    val user = AuthManager.auth.currentUser
                    isSignedIn = user != null
                }

                if (isSignedIn) {
                    UserProfileScreen(onSignedOut = {
                        isSignedIn = false
                    })
                } else {
                    SignInScreen(
                        onSignedIn = { isSignedIn = true },
                        onError = { err -> /* 토스트/로깅 처리: 필요하면 추가 */ }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // 화면 시작 시 auth 상태 체크(필요 시 Compose 상태와 동기화)
        // val currentUser = AuthManager.auth.currentUser
    }
}

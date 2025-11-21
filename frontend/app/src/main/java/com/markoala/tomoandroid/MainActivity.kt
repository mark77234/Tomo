package com.markoala.tomoandroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.navigation.AppNavHost
import com.markoala.tomoandroid.navigation.Screen
import com.markoala.tomoandroid.ui.components.ToastProvider
import com.markoala.tomoandroid.ui.theme.TomoAndroidTheme

class MainActivity : ComponentActivity() {
    private val deepLinkInviteCode = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)

        AuthManager.init()
        AuthManager.initTokenManager(this)


        // 딥링크 처리
        handleDeepLink(intent)

        setContent {
            TomoAndroidTheme {
                MainScreen(
                    inviteCode = deepLinkInviteCode.value
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null && data.scheme == "tomoapp" && data.host == "invite") {
            // tomoapp://invite/{inviteCode} 형식에서 inviteCode 추출
            val inviteCode = data.pathSegments.firstOrNull()
            deepLinkInviteCode.value = inviteCode
        }
    }
}

@Composable
fun MainScreen(inviteCode: String? = null) {
    var signedIn by remember { mutableStateOf(false) }
    val navController = rememberNavController()
    val context = LocalContext.current

    // 앱 시작 시 저장된 토큰 확인
    LaunchedEffect(Unit) {
        signedIn = AuthManager.hasValidTokens()

        // 401 에러 발생 시 로그인 페이지로 이동하는 콜백 설정
        AuthManager.setUnauthorizedCallback {
            signedIn = false
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Profile.route) { inclusive = true }
            }
        }
    }

    ToastProvider {
        // 로그인/로그아웃 시 signedIn 상태 변경
        AppNavHost(
            navController = navController,
            isSignedIn = signedIn,
            deepLinkInviteCode = inviteCode,
            context = context
        )
    }
}

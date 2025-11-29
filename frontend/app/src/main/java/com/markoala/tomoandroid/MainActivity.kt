package com.markoala.tomoandroid

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.navigation.AppNavHost
import com.markoala.tomoandroid.navigation.Screen
import com.markoala.tomoandroid.ui.components.ToastProvider
import com.markoala.tomoandroid.ui.theme.TomoAndroidTheme

class MainActivity : ComponentActivity() {
    private val deepLinkInviteCode = mutableStateOf<String?>(null)

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 권한 허용됨 → 알림 사용 가능
            } else {
                // 권한 거부됨 → 알림 비활성 안내 필요
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)

        // Notification Channel 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)

            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        askNotificationPermission()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            val msg = "FCM Token: $token"

            Log.d(TAG, msg)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

            // 필요하다면 서버로 토큰 전송
            // AuthManager.sendFcmTokenToServer(token)
        }


        AuthManager.init()
        AuthManager.initTokenManager(this)


        // 딥링크 처리
        handleDeepLink(intent)




        setContent {
            TomoAndroidTheme {
                RouteScreen(
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
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 이미 권한 허용됨
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // 👉 사용자에게 설명 UI 제공 (원한다면 구현)
                }

                else -> {
                    // 권한 요청
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
        }
}

@Composable
fun RouteScreen(inviteCode: String? = null) {
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

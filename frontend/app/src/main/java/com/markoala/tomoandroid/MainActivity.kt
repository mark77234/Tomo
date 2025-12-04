package com.markoala.tomoandroid

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Intent
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.data.repository.AuthRepository
import com.markoala.tomoandroid.data.repository.UserRepository
import com.markoala.tomoandroid.navigation.AppNavHost
import com.markoala.tomoandroid.navigation.Screen
import com.markoala.tomoandroid.ui.components.ToastProvider
import com.markoala.tomoandroid.ui.theme.TomoAndroidTheme
import com.markoala.tomoandroid.utils.NotificationPermissionHelper
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val deepLinkInviteCode = mutableStateOf<String?>(null)

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

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            val msg = "FCM Token: $token"

            Log.d(TAG, msg)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

            lifecycleScope.launch {
                try {
                    val profile = AuthRepository.getCurrentUserProfile()
                    if (profile != null) {
                        UserRepository.updateFcmToken(profile.uuid, token)
                        Log.d(TAG, "앱 시작 시 Firestore에 FCM 토큰 동기화 완료")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Firestore FCM 토큰 업데이트 실패", e)
                }
            }
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
}

@Composable
fun RouteScreen(inviteCode: String? = null) {
    var signedIn by remember { mutableStateOf(false) }
    val navController = rememberNavController()
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        NotificationPermissionHelper.markPermissionRequested(context)
    }

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
    LaunchedEffect(signedIn) {
        if (signedIn && NotificationPermissionHelper.shouldShowInitialPrompt(context)) {
            NotificationPermissionHelper.markPermissionRequested(context)
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    ToastProvider {
        // 로그인/로그아웃 시 signedIn 상태 변경
        AppNavHost(
            navController = navController,
            isSignedIn = signedIn,
            deepLinkInviteCode = inviteCode,
            context = context,
            onLoginSuccess = { signedIn = true },
            onLogout = { signedIn = false }
        )
    }
}

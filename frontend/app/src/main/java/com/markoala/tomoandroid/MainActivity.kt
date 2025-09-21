package com.markoala.tomoandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.navigation.AppNavHost
import com.markoala.tomoandroid.ui.theme.TomoAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AuthManager.init()

        setContent {
            TomoAndroidTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var signedIn by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    // 로그인/로그아웃 시 signedIn 상태 변경
    AppNavHost(navController = navController, isSignedIn = signedIn)

    // 로그인 성공/실패 콜백을 NavHost에서 처리하므로 아래 기존 UI는 제거
}

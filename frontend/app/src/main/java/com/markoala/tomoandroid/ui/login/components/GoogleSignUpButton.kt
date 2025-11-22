package com.markoala.tomoandroid.ui.login.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.ui.components.LoadingDialog
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.utils.auth.GoogleSignInCoordinator
import kotlinx.coroutines.launch

@Composable
fun GoogleSignUpButton(onSignedIn: () -> Unit) {
    val context = LocalContext.current
    val toastManager = LocalToastManager.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        coroutineScope.launch {
            GoogleSignInCoordinator.handleGoogleSignInResult(
                data = result.data,
                context = context,
                toastManager = toastManager,
                coroutineScope = coroutineScope,
                setLoading = { isLoading = it },
                onSignedIn = onSignedIn
            )
        }
    }

    if (isLoading) {
        LoadingDialog()
    }

    // Google 버튼 가이드라인 색상 (라이트 테마 기준) :contentReference[oaicite:2]{index=2}
    val backgroundColor = Color.White
    val contentColor = Color(0xFF1F1F1F) // 텍스트/아이콘 색상
    val borderStroke = BorderStroke(1.dp, Color(0xFF747775))

    Button(
        onClick = {
            coroutineScope.launch {
                GoogleSignInCoordinator.startGoogleSignIn(
                    context = context,
                    toastManager = toastManager,
                    setLoading = { isLoading = it },
                    launchIntent = { intent -> signInLauncher.launch(intent) }
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        border = borderStroke,
        contentPadding = ButtonDefaults.ContentPadding,

    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = "Google Logo",
            tint = Color.Unspecified, // Google G 로고는 색상 변경 금지. :contentReference[oaicite:3]{index=3}
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text ="Google 계정으로 로그인", color = contentColor)
    }
}

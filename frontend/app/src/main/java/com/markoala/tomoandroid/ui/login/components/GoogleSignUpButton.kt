package com.markoala.tomoandroid.ui.login.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
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

    CustomButton(
        text = if (isLoading) "연결 중..." else "Google 계정으로 로그인",
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
        enabled = !isLoading,
        style = ButtonStyle.Primary,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "Google Logo",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    )
}

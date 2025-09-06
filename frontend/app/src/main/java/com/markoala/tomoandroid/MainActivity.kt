package com.markoala.tomoandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.auth.CredentialSignInScreen
import com.markoala.tomoandroid.ui.theme.TomoAndroidTheme
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!signedIn) {
            CredentialSignInScreen { signedIn = true }
        } else {
            Text(text = "Signed in!")
            Button(onClick = {
                scope.launch {
                    val (success, _) = AuthManager.signOutSuspend(context)
                    if (success) signedIn = false
                }
            }) {
                Text(text = "Sign out")
            }
        }
    }
}

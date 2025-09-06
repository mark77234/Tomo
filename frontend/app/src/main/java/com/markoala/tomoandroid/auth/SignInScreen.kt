package com.markoala.tomoandroid.auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException

private const val TAG = "SignInScreen"

@Composable
fun SignInScreen(
    onSignedIn: () -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current

    // AuthManager 초기화 (Application 또는 Activity context로 한 번)
    LaunchedEffect(Unit) {
        AuthManager.init(context)
    }

    // GoogleSignInOptions: 반드시 web client id(requestIdToken)에 Web client id 사용
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(com.markoala.tomoandroid.R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent: Intent? = result.data
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (!idToken.isNullOrEmpty()) {
                    AuthManager.firebaseAuthWithGoogle(idToken) { success, err ->
                        if (success) {
                            onSignedIn()
                        } else {
                            onError(err ?: "Firebase sign-in failed")
                        }
                    }
                } else {
                    onError("idToken is null")
                }
            } catch (e: ApiException) {
                Log.e(TAG, "Google sign-in failed", e)
                onError("Google sign-in error: ${e.statusCode} ${e.localizedMessage}")
            }
        } else {
            onError("Activity cancelled or failed: ${result.resultCode}")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            // signInIntent를 통해 Google 계정 선택/동의 UI 띄움
            launcher.launch(googleSignInClient.signInIntent)
        }) {
            Text(text = "Sign in with Google")
        }
    }
}

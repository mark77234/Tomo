// 파일: CredentialSignInScreen.kt (대체)
package com.markoala.tomoandroid.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest

@Composable
fun CredentialSignInScreen(onSignedIn: () -> Unit) {
    val context = LocalContext.current
    val activity = context as Activity
    val signInClient = remember { Identity.getSignInClient(activity) }

    // IntentSender launcher
    val launcher = rememberLauncherForActivityResult(StartIntentSenderForResult()) { activityResult ->
        val data = activityResult.data
        try {
            // getSignInCredentialFromIntent는 SignInClient 구현에서 제공하는 helper
            val credential = signInClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            if (!idToken.isNullOrEmpty()) {
                AuthManager.firebaseAuthWithGoogle(idToken) { success, err ->
                    if (success) onSignedIn()
                    else Toast.makeText(context, "Firebase Sign-in failed: $err", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "No id token returned", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Sign-in cancelled / failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            // 서버용 Web client id(=default_web_client_id) 사용
            val request = GetSignInIntentRequest.builder()
                .setServerClientId(context.getString(com.markoala.tomoandroid.R.string.default_web_client_id))
                .build()

            signInClient.getSignInIntent(request)
                .addOnSuccessListener { pendingIntent ->
                    // PendingIntent의 IntentSender를 통해 계정 선택 UI를 띄움
                    val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                    launcher.launch(intentSenderRequest)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to start sign-in: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
        }) {
            Text(text = "Sign in with Google")
        }
    }
}

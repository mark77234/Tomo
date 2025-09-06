package com.markoala.tomoandroid.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.markoala.tomoandroid.auth.AuthManager

@Composable
fun UserProfileScreen(onSignedOut: () -> Unit) {
    val user = AuthManager.auth.currentUser
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "UID: ${user?.uid ?: "N/A"}")
        Text(text = "Email: ${user?.email ?: "N/A"}")
        Button(onClick = {
            // 로그아웃
            AuthManager.signOut(Firebase.auth.app.applicationContext) { success, msg ->
                onSignedOut()
            }
        }) {
            Text("Sign out")
        }
    }
}


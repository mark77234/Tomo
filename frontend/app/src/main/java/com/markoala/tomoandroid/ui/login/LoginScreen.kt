package com.markoala.tomoandroid.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.markoala.tomoandroid.auth.CredentialSignInScreen
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType

@Composable
fun LoginScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),

        ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomText(
                text = "토모",
                type = CustomTextType.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))
            CredentialSignInScreen(
                onSignedIn = {
                    navController.navigate("profile") {
                        popUpTo("login") { inclusive = true }
                    }
                },
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}

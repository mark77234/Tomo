package com.markoala.tomoandroid.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.auth.GoogleSignUpButton

import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun LoginScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .safeContentPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomText(
                text = "토모",
                type = CustomTextType.headlineSmall
            )
            CustomText(
                text = "친구와의 우정을 기록하세요",
                type = CustomTextType.bodyMedium,
                color = CustomColor.mediumGray
            )

            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = CustomColor.lightGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomText(
                    text = "로그인",
                    type = CustomTextType.titleMedium
                )
                CustomText(
                    text = "계정에 로그인하여 친구들과 약속을 만들고 관리하세요.",
                    type = CustomTextType.bodyMedium,
                    color = CustomColor.mediumGray,
                    modifier = Modifier
                        .widthIn(max = 240.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                GoogleSignUpButton(
                    onSignedIn = {
                        navController.navigate("profile") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                )


            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}

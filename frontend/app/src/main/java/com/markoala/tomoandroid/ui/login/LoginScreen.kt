package com.markoala.tomoandroid.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.login.components.GoogleSignUpButton
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun LoginScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.white)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_tomo),
            contentDescription = "Tomo Logo",
            modifier = Modifier.width(120.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomText(
            text = "친구와의 우정을 기록하세요",
            type = CustomTextType.body,
            color = CustomColor.textSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            color = CustomColor.white
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomText(
                    text = "토모에 오신 것을 환영해요",
                    type = CustomTextType.title,
                    color = CustomColor.textPrimary
                )
                CustomText(
                    text = "Google 계정으로 간편하게 시작하세요.",
                    type = CustomTextType.body,
                    color = CustomColor.textSecondary,
                    textAlign = TextAlign.Center
                )
                GoogleSignUpButton(
                    onSignedIn = {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
                HorizontalDivider(color = CustomColor.outline)
                CustomButton(
                    text = "이메일로 로그인 (준비중)",
                    onClick = {},
                    style = ButtonStyle.Primary,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    leadingIcon = painterResource(R.drawable.ic_email)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        CustomText(
            text = "로그인하면 토모의 이용약관과 개인정보처리방침에 동의하게 됩니다.",
            type = CustomTextType.bodySmall,
            color = CustomColor.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}

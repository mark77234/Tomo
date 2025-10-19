package com.markoala.tomoandroid.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextField
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.ProfileImage
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun ProfileScreen(
    name: String,
    email: String,
    userId: String,
    paddingValues: PaddingValues,
    onSaveProfile: (String, String) -> Unit = { _, _ -> }
) {
    var profileName by remember { mutableStateOf(name) }
    var profileEmail by remember { mutableStateOf(email) }

    LaunchedEffect(name) {
        profileName = name
    }
    LaunchedEffect(email) {
        profileEmail = email
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            CustomText(
                text = "내 정보",
                type = CustomTextType.headlineLarge,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            ProfileImage(
                size = 80.dp,
                imageUrl = null // 기본 아이콘 표시
            )
        }

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "기본 프로필 아이콘",
                    tint = CustomColor.gray200,
                    modifier = Modifier
                        .size(24.dp),
                )
                CustomText(
                    text = "이름",
                    type = CustomTextType.bodyLarge,
                    color = CustomColor.gray200,
                    fontSize = 14.sp,
                )
            }

            CustomTextField(
                value = profileName,
                onValueChange = { profileName = it },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                cornerRadius = 8,
                unfocusedBorderColor = CustomColor.gray100,
                focusedBorderColor = CustomColor.gray100,
                placeholder = "이름"
            )
        }

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_email),
                    contentDescription = "이메일",
                    tint = CustomColor.gray200,
                    modifier = Modifier
                        .size(20.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                CustomText(
                    text = "이메일",
                    type = CustomTextType.bodyLarge,
                    color = CustomColor.gray200,
                    fontSize = 14.sp,
                )
            }

            CustomTextField(
                value = profileEmail,
                onValueChange = { },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                cornerRadius = 8,
                unfocusedBorderColor = CustomColor.gray100,
                focusedBorderColor = CustomColor.gray100,
                placeholder = "이메일"
            )
        }


    }
}

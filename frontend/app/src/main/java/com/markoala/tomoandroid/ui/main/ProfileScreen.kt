package com.markoala.tomoandroid.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
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
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {}
) {
    var profileName by remember { mutableStateOf(name) }
    var profileEmail by remember { mutableStateOf(email) }

    LaunchedEffect(name) { profileName = name }
    LaunchedEffect(email) { profileEmail = email }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CustomColor.background)
            .padding(paddingValues)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = CustomColor.textPrimary,
                    modifier = Modifier
                        .size(24.dp)
                        .clickableWithoutRipple { onClose() }
                )
                CustomText(text = "내 프로필", type = CustomTextType.title, color = CustomColor.textPrimary)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            color = CustomColor.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileImage(size = 96.dp)
                CustomText(text = profileName.ifBlank { "이름 없음" }, type = CustomTextType.title, color = CustomColor.textPrimary)
                CustomText(text = "ID: ${userId.takeLast(6)}", type = CustomTextType.bodySmall, color = CustomColor.textSecondary)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CustomText(text = "이름", type = CustomTextType.bodySmall, color = CustomColor.textSecondary)
                CustomTextField(value = profileName, onValueChange = {}, enabled = false)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CustomText(text = "이메일", type = CustomTextType.bodySmall, color = CustomColor.textSecondary)
                CustomTextField(value = profileEmail, onValueChange = {}, enabled = false)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        CustomButton(text = "닫기", onClick = onClose, style = ButtonStyle.Secondary)
    }
}

private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    clickable(indication = null, interactionSource = interactionSource) { onClick() }
}

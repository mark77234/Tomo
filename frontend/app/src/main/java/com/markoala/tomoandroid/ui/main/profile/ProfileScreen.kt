package com.markoala.tomoandroid.ui.main.profile

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
import com.markoala.tomoandroid.util.generateInviteCode

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
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {


        CustomText(
            text = "내 프로필",
            type = CustomTextType.headline,
            color = CustomColor.textPrimary
        )

        CustomText(
            text = "프로필 정보를 확인하세요",
            type = CustomTextType.bodySmall,
            color = CustomColor.textSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 프로필 카드
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ProfileImage(size = 96.dp)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomText(
                        text = profileName.ifBlank { "이름 없음" },
                        type = CustomTextType.headline,
                        color = CustomColor.textPrimary
                    )
                    CustomText(
                        text = "초대코드: ${generateInviteCode(userId)}",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.textSecondary
                    )
                }
            }
        }

        // 정보 필드
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CustomText(
                        text = "이름",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.textSecondary
                    )
                    CustomTextField(
                        value = profileName,
                        onValueChange = {},
                        enabled = false
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CustomText(
                        text = "이메일",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.textSecondary
                    )
                    CustomTextField(
                        value = profileEmail,
                        onValueChange = {},
                        enabled = false
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 버튼
        CustomButton(
            text = "닫기",
            onClick = onClose,
            style = ButtonStyle.Secondary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    clickable(indication = null, interactionSource = interactionSource) { onClick() }
}

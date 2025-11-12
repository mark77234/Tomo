package com.markoala.tomoandroid.ui.main.friends.add_friends.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.util.generateInviteCode

@Composable
fun ShareInviteSection(userId: String, onCopy: () -> Unit) {
    val inviteCode = generateInviteCode(userId)
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = CustomColor.surface,
            border = BorderStroke(1.dp, CustomColor.outline)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomText(text = "초대 코드", type = CustomTextType.title, color = CustomColor.textPrimary)
                CustomText(text = inviteCode, type = CustomTextType.display, color = CustomColor.textPrimary)
                CustomText(
                    text = "친구에게 코드를 공유하면 간편하게 추가할 수 있어요.",
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )
            }
        }
        CustomButton(text = "초대 코드 복사", onClick = { onCopy() }, style = ButtonStyle.Primary)
    }
}


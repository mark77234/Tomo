package com.markoala.tomoandroid.ui.main.home.components

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

@Composable
fun HeroCard(userName: String, onPlanMeetingClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = CustomColor.primaryContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomText(
                text = if (userName.isNotBlank()) "${userName}님, 새로운 모임을 열어볼까요?" else "오늘 어떤 만남을 기록할까요?",
                type = CustomTextType.title,
                color = CustomColor.primary
            )
            CustomText(
                text = "친구들과 모임을 계획하고 특별한 순간을 함께하세요.",
                type = CustomTextType.bodySmall,
                color = CustomColor.primaryDim
            )
            CustomButton(
                text = "모임 생성",
                onClick = onPlanMeetingClick,
                modifier = Modifier.fillMaxWidth(),
                style = ButtonStyle.Primary
            )
        }
    }
}

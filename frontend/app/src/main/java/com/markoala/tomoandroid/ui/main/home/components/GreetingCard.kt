package com.markoala.tomoandroid.ui.main.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun GreetingCard(userName: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        CustomText(
            text = if (userName.isNotBlank()) "안녕하세요, $userName 님" else "오늘은 어떤 추억을 남길까요?",
            type = CustomTextType.display,
            color = CustomColor.textPrimary
        )
        CustomText(
            text = "따뜻한 우정을 기록해 보세요",
            type = CustomTextType.body,
            color = CustomColor.textSecondary
        )
    }
}

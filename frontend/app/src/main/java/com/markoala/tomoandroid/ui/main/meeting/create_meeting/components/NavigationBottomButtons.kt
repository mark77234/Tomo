package com.markoala.tomoandroid.ui.main.meeting.create_meeting.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton

@Composable
fun NavigationBottomButtons(
    currentStep: Int,
    isLoading: Boolean,
    canGoNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (currentStep > 1) {
            CustomButton(
                text = "이전",
                onClick = onPrevious,
                style = ButtonStyle.Secondary,
                modifier = Modifier.weight(1f)
            )
        }
        CustomButton(
            text = when {
                isLoading -> "생성 중..."
                currentStep < 3 -> "다음"
                else -> "모임 만들기"
            },
            onClick = onNext,
            enabled = canGoNext && !isLoading,
            modifier = Modifier.weight(1f)
        )
    }
}

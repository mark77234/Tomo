package com.markoala.tomoandroid.ui.main.meeting.create_meeting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun StepIndicator(currentStep: Int) {
    val steps = listOf("기본 정보", "친구 초대", "확인")
    Column(modifier = Modifier.fillMaxWidth()) {
        // 상단: 원 + 커넥터 라인
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, _ ->
                val stepNumber = index + 1
                val isCompleted = currentStep > stepNumber
                val isActive = currentStep == stepNumber

                // 원
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = when {
                        isActive -> CustomColor.primary
                        isCompleted -> CustomColor.primary.copy(alpha = 0.15f)
                        else -> CustomColor.surface
                    },
                    contentColor = when {
                        isActive -> CustomColor.white
                        isCompleted -> CustomColor.primary
                        else -> CustomColor.textSecondary
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CustomText(
                            text = stepNumber.toString(),
                            type = CustomTextType.bodySmall,
                            color = Color.Unspecified
                        )
                    }
                }

                // 라인 커넥터 (마지막 원 뒤에는 없음)
                if (index < steps.lastIndex) {
                    val connectorColor = when {
                        isCompleted || isActive -> CustomColor.primary
                        else -> CustomColor.outline
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(connectorColor)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 하단: 라벨들
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, title ->
                val stepNumber = index + 1
                val isActive = currentStep == stepNumber
                val isCompleted = currentStep > stepNumber
                CustomText(
                    text = title,
                    type = CustomTextType.bodySmall,
                    color = when {
                        isActive -> CustomColor.textPrimary
                        isCompleted -> CustomColor.textSecondary
                        else -> CustomColor.textSecondary
                    }
                )
            }
        }
    }
}

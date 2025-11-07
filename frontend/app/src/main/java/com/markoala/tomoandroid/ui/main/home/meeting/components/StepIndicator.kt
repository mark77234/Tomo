package com.markoala.tomoandroid.ui.main.home.meeting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        steps.forEachIndexed { index, title ->
            val stepNumber = index + 1
            val isCompleted = currentStep > stepNumber
            val isActive = currentStep == stepNumber
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (index != 0) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (isCompleted || isActive) CustomColor.primary else CustomColor.outline)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Surface(
                        modifier = Modifier.size(40.dp),
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
                    if (index != steps.lastIndex) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (isCompleted) CustomColor.primary else CustomColor.outline)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
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

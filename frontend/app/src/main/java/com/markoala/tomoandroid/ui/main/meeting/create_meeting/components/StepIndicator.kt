package com.markoala.tomoandroid.ui.main.meeting.create_meeting.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor
import kotlin.math.roundToInt

@Composable
fun StepIndicator(currentStep: Int) {
    val steps = listOf("기본 정보", "친구 초대", "확인")
    val circleSize = 36.dp
    val totalSegments = (steps.size - 1).coerceAtLeast(1)
    val completedSegments = (currentStep - 1).coerceIn(0, steps.size - 1)
    val progressFraction = completedSegments / totalSegments.toFloat()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp)
    ) {
        val strokeWidth = 2.dp

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(circleSize)
                .align(Alignment.TopCenter)
                .zIndex(-1f)
        ) {
            // 가로 라인을 원 뒤로 배치
            Canvas(modifier = Modifier.matchParentSize()) {
                val radius = circleSize.toPx() / 2f
                val startX = radius
                val endX = size.width - radius
                val centerY = size.height / 2f
                val stroke = strokeWidth.toPx()

                if (endX > startX) {
                    drawLine(
                        color = CustomColor.outline,
                        start = Offset(startX, centerY),
                        end = Offset(endX, centerY),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round
                    )

                    if (progressFraction > 0f) {
                        val progressX = startX + (endX - startX) * progressFraction
                        drawLine(
                            color = CustomColor.primary,
                            start = Offset(startX, centerY),
                            end = Offset(progressX, centerY),
                            strokeWidth = stroke,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }

        // 실제 UI (동그라미 + 라벨)
        Layout(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .zIndex(1f),
            content = {
                steps.forEachIndexed { index, label ->
                    val stepNumber = index + 1
                    val isCompleted = currentStep > stepNumber
                    val isActive = currentStep == stepNumber

                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // 동그라미
                        Surface(
                            modifier = Modifier.size(circleSize),
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

                        Spacer(modifier = Modifier.height(6.dp))

                        // 라벨
                        CustomText(
                            text = label,
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
        ) { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0)) }
            val maxHeight = placeables.maxOf { it.height }
            val circlePx = circleSize.roundToPx()
            val startCenter = circlePx / 2f
            val endCenter = constraints.maxWidth - circlePx / 2f
            val spacing = if (steps.size > 1) (endCenter - startCenter) / (steps.size - 1) else 0f

            layout(constraints.maxWidth, maxHeight) {
                placeables.forEachIndexed { index, placeable ->
                    val centerX = startCenter + spacing * index
                    val childX = (centerX - placeable.width / 2f)
                        .roundToInt()
                        .coerceIn(0, constraints.maxWidth - placeable.width)
                    placeable.placeRelative(childX, 0)
                }
            }
        }
    }

}

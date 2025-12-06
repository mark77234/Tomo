package com.markoala.tomoandroid.ui.main.meeting.create_meeting.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
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
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(circleSize)
                .align(Alignment.TopCenter)
        ) {
            val strokeWidth = 2.dp.toPx()
            val circleRadius = circleSize.toPx() / 2f
            val lineInset = circleRadius + strokeWidth
            val startCenter = circleRadius
            val endCenter = size.width - circleRadius
            val spacing =
                if (steps.size > 1) (endCenter - startCenter) / (steps.size - 1) else 0f
            val centerY = circleRadius

            repeat(steps.size - 1) { index ->
                val fromCenter = startCenter + spacing * index
                val toCenter = fromCenter + spacing
                val lineStart = fromCenter + lineInset
                val lineEnd = toCenter - lineInset

                if (lineEnd > lineStart) {
                    drawLine(
                        color = CustomColor.outline,
                        start = Offset(lineStart, centerY),
                        end = Offset(lineEnd, centerY),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            if (progressFraction > 0f) {
                val segmentDrawableLength =
                    (spacing - lineInset * 2f).coerceAtLeast(0f)
                val totalSegments = (steps.size - 1).coerceAtLeast(1)
                val totalDrawableLength = segmentDrawableLength * totalSegments
                var remainingProgress = totalDrawableLength * progressFraction

                repeat(steps.size - 1) { index ->
                    val fromCenter = startCenter + spacing * index
                    val lineStart = fromCenter + lineInset
                    val segmentLength = segmentDrawableLength

                    if (remainingProgress <= 0f || segmentLength <= 0f) return@repeat

                    val drawLength = remainingProgress.coerceAtMost(segmentLength)
                    drawLine(
                        color = CustomColor.primary,
                        start = Offset(lineStart, centerY),
                        end = Offset(lineStart + drawLength, centerY),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    remainingProgress -= drawLength
                }
            }
        }
        // 실제 UI (동그라미 + 라벨)
        Layout(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            content = {
                steps.forEachIndexed { index, label ->
                    val stepNumber = index + 1
                    val isCompleted = currentStep > stepNumber
                    val isActive = currentStep == stepNumber
                    val circleFill = when {
                        isActive -> CustomColor.primary
                        isCompleted -> CustomColor.primary.copy(alpha = 0.15f)
                        else -> CustomColor.surface
                    }
                    val textColor = when {
                        isActive -> CustomColor.white
                        isCompleted -> CustomColor.primary
                        else -> CustomColor.textSecondary
                    }

                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // 동그라미
                        Surface(
                            modifier = Modifier.size(circleSize),
                            shape = CircleShape,
                            color = CustomColor.surface,
                            contentColor = Color.Unspecified
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(circleFill, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                CustomText(
                                    text = stepNumber.toString(),
                                    type = CustomTextType.bodySmall,
                                    color = textColor
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

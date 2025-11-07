package com.markoala.tomoandroid.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.theme.CustomColor

/**
 * 점선 테두리를 가진 컨테이너 컴포넌트
 */
@Composable
fun DashedBorderBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    borderColor: Color = CustomColor.outline,
    borderWidth: Dp = 1.dp,
    dashLength: Float = 5f,
    gapLength: Float = 5f,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        // 컨텐츠
        content()

        // 점선 테두리
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
        ) {
            val strokeWidth = borderWidth.toPx()

            when (shape) {
                is RoundedCornerShape -> {
                    val cornerRadius = 16.dp.toPx() // 기본값, 필요시 매개변수로 추가 가능
                    val path = Path().apply {
                        addRoundRect(
                            androidx.compose.ui.geometry.RoundRect(
                                left = strokeWidth / 2,
                                top = strokeWidth / 2,
                                right = size.width - strokeWidth / 2,
                                bottom = size.height - strokeWidth / 2,
                                radiusX = cornerRadius,
                                radiusY = cornerRadius
                            )
                        )
                    }

                    drawPath(
                        path = path,
                        color = borderColor,
                        style = Stroke(
                            width = strokeWidth,
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(
                                    dashLength,
                                    gapLength
                                ), 0f
                            )
                        )
                    )
                }

                else -> {
                    // 기본 사각형
                    val path = Path().apply {
                        addRect(
                            androidx.compose.ui.geometry.Rect(
                                left = strokeWidth / 2,
                                top = strokeWidth / 2,
                                right = size.width - strokeWidth / 2,
                                bottom = size.height - strokeWidth / 2
                            )
                        )
                    }

                    drawPath(
                        path = path,
                        color = borderColor,
                        style = Stroke(
                            width = strokeWidth,
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(
                                    dashLength,
                                    gapLength
                                ), 0f
                            )
                        )
                    )
                }
            }
        }
    }
}

/**
 * 점선 원형 테두리 컴포넌트 (아이콘용)
 */
@Composable
fun DashedCircleBorder(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    borderColor: Color = CustomColor.outline,
    borderWidth: Dp = 1.dp,
    dashLength: Float = 5f,
    gapLength: Float = 5f,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.size(size)) {
        // 컨텐츠
        content()

        // 점선 원형 테두리
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val strokeWidth = borderWidth.toPx()
            val centerX = center.x
            val centerY = center.y
            val radius = minOf(centerX, centerY) - strokeWidth / 2

            drawCircle(
                color = borderColor,
                radius = radius,
                center = center,
                style = Stroke(
                    width = strokeWidth,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), 0f)
                )
            )
        }
    }
}

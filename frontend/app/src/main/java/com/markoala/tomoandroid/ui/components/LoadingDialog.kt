package com.markoala.tomoandroid.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun LoadingDialog() {
    Dialog(
        onDismissRequest = {},
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        // 🔥 전체를 아주 은은하게 어둡게 처리
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x08000000)), // 훨씬 연한 dim
            contentAlignment = Alignment.Center
        ) {
            MorphingDots() // 🔥 점만 단독 표시
        }
    }
}

/**
 * 부드럽게 크기가 변하면서 이어지는 3개의 점 애니메이션
 * Vibrant / fluid / smooth 느낌
 */
@Composable
private fun MorphingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(0)
        ),
        label = "dot1"
    )

    val scale2 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(120) // 살짝 뒤따라감
        ),
        label = "dot2"
    )

    val scale3 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(240) // 더 뒤따라감
        ),
        label = "dot3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Dot(scale = scale1, color = Color(0xFFDC9E6A)) // 따뜻한 브라운톤
        Dot(scale = scale2, color = Color(0xFFF2B88B))
        Dot(scale = scale3, color = Color(0xFFFAE0B8))
    }
}

@Composable
private fun Dot(scale: Float, color: Color) {
    Box(
        modifier = Modifier
            .size(14.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .alpha(0.5f + (scale - 0.6f)) // 커질수록 살짝 더 진해지게
            .background(color = color, shape = CircleShape)
    )
}

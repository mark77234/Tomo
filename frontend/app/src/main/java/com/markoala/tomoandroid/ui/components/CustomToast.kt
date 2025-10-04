package com.markoala.tomoandroid.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

enum class ToastType {
    SUCCESS,
    ERROR,
    WARNING,
    INFO
}

data class ToastConfig(
    val type: ToastType,
    val message: String,
    val duration: Long = 3000L
)

@Composable
fun CustomToast(
    config: ToastConfig?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    AnimatedVisibility(
        visible = config != null,
        enter = slideInVertically {
            with(density) { -40.dp.roundToPx() }
        } + fadeIn(),
        exit = slideOutVertically {
            with(density) { -40.dp.roundToPx() }
        } + fadeOut(),
        modifier = modifier
    ) {
        config?.let { toastConfig ->
            LaunchedEffect(toastConfig) {
                delay(toastConfig.duration)
                onDismiss()
            }

            ToastContent(config = toastConfig)
        }
    }
}

@Composable
private fun ToastContent(
    config: ToastConfig,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, iconColor, icon) = when (config.type) {
        ToastType.SUCCESS -> Triple(
            Color(0xFF4CAF50),
            Color.White,
            Icons.Default.CheckCircle
        )

        ToastType.ERROR -> Triple(
            Color(0xFFF44336),
            Color.White,
            Icons.Filled.Warning
        )

        ToastType.WARNING -> Triple(
            Color(0xFFFF9800),
            Color.White,
            Icons.Default.Warning
        )

        ToastType.INFO -> Triple(
            Color(0xFF2196F3),
            Color.White,
            Icons.Default.Info
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            CustomText(
                text = config.message,
                type = CustomTextType.bodyMedium,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// 토스트를 쉽게 사용할 수 있는 helper class
class ToastManager {
    private var _currentToast = mutableStateOf<ToastConfig?>(null)
    val currentToast: State<ToastConfig?> = _currentToast

    fun showToast(
        message: String,
        type: ToastType = ToastType.INFO,
        duration: Long = 3000L
    ) {
        _currentToast.value = ToastConfig(
            type = type,
            message = message,
            duration = duration
        )
    }

    fun dismissToast() {
        _currentToast.value = null
    }

    fun showSuccess(message: String, duration: Long = 3000L) {
        showToast(message, ToastType.SUCCESS, duration)
    }

    fun showError(message: String, duration: Long = 3000L) {
        showToast(message, ToastType.ERROR, duration)
    }

    fun showWarning(message: String, duration: Long = 3000L) {
        showToast(message, ToastType.WARNING, duration)
    }

    fun showInfo(message: String, duration: Long = 3000L) {
        showToast(message, ToastType.INFO, duration)
    }
}

// CompositionLocal을 통한 전역 토스트 관리
val LocalToastManager = compositionLocalOf<ToastManager> {
    error("ToastManager not provided")
}

@Composable
fun ToastProvider(
    toastManager: ToastManager = remember { ToastManager() },
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalToastManager provides toastManager) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()

            // 토스트를 화면 상단에 표시
            CustomToast(
                config = toastManager.currentToast.value,
                onDismiss = { toastManager.dismissToast() },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            )
        }
    }
}

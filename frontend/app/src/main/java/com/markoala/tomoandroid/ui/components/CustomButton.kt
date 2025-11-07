package com.markoala.tomoandroid.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material3.LocalContentColor
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import com.markoala.tomoandroid.ui.theme.CustomColor

enum class ButtonStyle {
    Primary,
    Secondary
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: ButtonStyle = ButtonStyle.Primary,
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "buttonPress"
    )

    val shape = RoundedCornerShape(999.dp)
    val contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
    val contentColor = when (style) {
        ButtonStyle.Primary -> CustomColor.white
        ButtonStyle.Secondary -> CustomColor.textBody
    }

    val disabledContentColor = when (style) {
        ButtonStyle.Primary -> CustomColor.white.copy(alpha = 0.6f)
        ButtonStyle.Secondary -> CustomColor.textSecondary
    }

    val content: @Composable () -> Unit = {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingIcon?.invoke()
                CustomText(
                    text = text,
                    type = CustomTextType.button,
                    color = LocalContentColor.current
                )
                trailingIcon?.invoke()
            }
        }
    }

    val scaledModifier = modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }

    when (style) {
        ButtonStyle.Primary -> Button(
            onClick = onClick,
            enabled = enabled,
            modifier = scaledModifier,
            shape = shape,
            contentPadding = contentPadding,
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomColor.primary,
                contentColor = contentColor,
                disabledContentColor = disabledContentColor,
                disabledContainerColor = CustomColor.primary.copy(alpha = 0.4f)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp, pressedElevation = 4.dp),
            interactionSource = interactionSource
        ) { content() }

        ButtonStyle.Secondary -> OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = scaledModifier,
            shape = shape,
            contentPadding = contentPadding,
            border = BorderStroke(1.dp, CustomColor.outline),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = contentColor,
                containerColor = CustomColor.background,
                disabledContainerColor = CustomColor.surface,
                disabledContentColor = disabledContentColor
            ),
            interactionSource = interactionSource
        ) { content() }
    }
}

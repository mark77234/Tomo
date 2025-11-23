package com.markoala.tomoandroid.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    singleLine: Boolean = true,
    supportingText: String? = null,
    leadingIcon: (@Composable (() -> Unit))? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.defaultMinSize(minHeight = 56.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        textStyle = MaterialTheme.typography.bodyLarge,
        enabled = enabled,
        singleLine = singleLine,
        interactionSource = interactionSource,
        leadingIcon = leadingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CustomColor.primary,
            unfocusedBorderColor = CustomColor.outline,
            disabledBorderColor = CustomColor.outline,
            focusedContainerColor = CustomColor.gray100,
            unfocusedContainerColor = CustomColor.white,
            disabledContainerColor = CustomColor.surface,
            cursorColor = CustomColor.primary,
            focusedTextColor = CustomColor.textBody,
            unfocusedTextColor = CustomColor.textBody,
            disabledTextColor = CustomColor.textSecondary,
            focusedPlaceholderColor = CustomColor.textSecondary,
            unfocusedPlaceholderColor = CustomColor.textSecondary,
            disabledPlaceholderColor = CustomColor.textSecondary
        ),
        placeholder = {
            if (placeholder.isNotBlank()) {
                CustomText(
                    text = placeholder,
                    type = CustomTextType.body,
                    color = CustomColor.textSecondary
                )
            }
        },
        supportingText = supportingText?.let {
            {
                CustomText(
                    text = it,
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )
            }
        }
    )
}

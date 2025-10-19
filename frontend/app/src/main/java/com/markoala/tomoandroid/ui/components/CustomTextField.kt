package com.markoala.tomoandroid.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
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
    unfocusedBorderColor: androidx.compose.ui.graphics.Color = CustomColor.gray100,
    focusedBorderColor: androidx.compose.ui.graphics.Color = CustomColor.gray300,
    cornerRadius: Int = 12
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius.dp),
        enabled = enabled,
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = unfocusedBorderColor,
            focusedBorderColor = focusedBorderColor
        ),
        placeholder = {
            CustomText(
                text = placeholder,
                type = CustomTextType.body,
                color = CustomColor.gray300
            )
        }
    )
}

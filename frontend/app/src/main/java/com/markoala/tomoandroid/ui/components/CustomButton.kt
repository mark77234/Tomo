package com.markoala.tomoandroid.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textColor: Color = CustomColor.black,
    backgroundColor: Color = CustomColor.white,
    borderColor: Color = CustomColor.gray100,
    borderWidth: Dp = 1.dp,
    cornerRadius: Dp = 14.dp,
    contentPadding: PaddingValues = PaddingValues(vertical = 12.dp),
    // 추가적으로 필요하면 icon, loading 등 파라미터 확장 가능
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
        ),
        border = BorderStroke(borderWidth, borderColor),
        shape = RoundedCornerShape(cornerRadius),
        contentPadding = contentPadding,
    ) {
        CustomText(
            text = text,
            type = CustomTextType.body,
            color = textColor
        )
    }
}


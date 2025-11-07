package com.markoala.tomoandroid.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.ui.theme.Pretendard

enum class CustomTextType {
    display,
    headline,
    title,
    body,
    bodySmall,
    label,
    button
}

private fun getTextStyle(type: CustomTextType): TextStyle = when (type) {
    CustomTextType.display -> TextStyle(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = Pretendard
    )

    CustomTextType.headline -> TextStyle(
        fontSize = 22.sp,
        lineHeight = 30.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = Pretendard
    )

    CustomTextType.title -> TextStyle(
        fontSize = 18.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = Pretendard
    )

    CustomTextType.body -> TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = Pretendard
    )

    CustomTextType.bodySmall -> TextStyle(
        fontSize = 14.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = Pretendard
    )

    CustomTextType.label -> TextStyle(
        fontSize = 12.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = Pretendard
    )

    CustomTextType.button -> TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = Pretendard
    )
}

@Composable
fun CustomText(
    text: String,
    modifier: Modifier = Modifier,
    type: CustomTextType = CustomTextType.body,
    style: TextStyle? = null,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    fontSize: androidx.compose.ui.unit.TextUnit? = null
) {
    var appliedStyle = style ?: getTextStyle(type)
    if (fontSize != null) {
        appliedStyle = appliedStyle.copy(fontSize = fontSize)
    }
    Text(
        text = text,
        modifier = modifier,
        style = appliedStyle,
        color = color,
        textAlign = textAlign
    )
}

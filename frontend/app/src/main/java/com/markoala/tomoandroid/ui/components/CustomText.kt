package com.markoala.tomoandroid.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.R

val Pretendard = FontFamily(
    Font(R.font.pretendard_thin, FontWeight.W100),
    Font(R.font.pretendard_extralight, FontWeight.W200),
    Font(R.font.pretendard_light, FontWeight.W300),
    Font(R.font.pretendard_regular, FontWeight.W400),
    Font(R.font.pretendard_medium, FontWeight.W500),
    Font(R.font.pretendard_semi_bold, FontWeight.W600),
    Font(R.font.pretendard_bold, FontWeight.W700),
    Font(R.font.pretendard_extra_bold, FontWeight.W800),
    Font(R.font.pretendard_black, FontWeight.W900)
)

enum class CustomTextType {
    display, headline, title, body, label
}

private fun getTextStyle(type: CustomTextType): TextStyle = when (type) {
    CustomTextType.display -> TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.W700,
        fontFamily = Pretendard
    )

    CustomTextType.headline -> TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.W700,
        fontFamily = Pretendard
    )

    CustomTextType.title -> TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.W600,
        fontFamily = Pretendard
    )

    CustomTextType.body -> TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W400,
        fontFamily = Pretendard
    )

    CustomTextType.label -> TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.W500,
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

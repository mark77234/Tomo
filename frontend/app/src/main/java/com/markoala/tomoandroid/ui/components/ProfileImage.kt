package com.markoala.tomoandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    imageUrl: String? = null
) {
    // 사이즈에 따른 아이콘 크기 비율 계산 (전체 크기의 약 81% = 65/80)
    val iconSize = size * 0.81f
    val iconPadding = size * 0.15f // 전체 크기의 15%

    DashedCircleBorder(
        modifier = modifier,
        size = size,
        borderColor = CustomColor.gray100,
        borderWidth = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = CustomColor.gray30,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl != null && imageUrl.isNotBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "프로필 이미지",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            color = CustomColor.gray30,
                            shape = CircleShape
                        )
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "기본 프로필 아이콘",
                    tint = CustomColor.gray200,
                    modifier = Modifier
                        .size(iconSize)
                        .padding(iconPadding)
                )
            }
        }
    }
}

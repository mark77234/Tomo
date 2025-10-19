package com.markoala.tomoandroid.ui.main.home.meeting.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun MeetingHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomText(
            text = "모임 생성",
            type = CustomTextType.headline,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = CustomColor.gray100,
                    shape = RoundedCornerShape(32.dp)
                )
                .clickable { onBackClick() },
            shape = RoundedCornerShape(32.dp),
            color = CustomColor.white
        ) {
            Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)) {
                CustomText(
                    text = "목록보기",
                    type = CustomTextType.title,
                    fontSize = 14.sp,
                    color = CustomColor.black
                )
            }
        }
    }
}
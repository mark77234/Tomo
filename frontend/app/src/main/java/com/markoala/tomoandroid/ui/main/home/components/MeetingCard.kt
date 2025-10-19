package com.markoala.tomoandroid.ui.main.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.data.model.moim.Meeting
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor


@Composable
fun MeetingCard(meeting: Meeting) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CustomColor.white),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                CustomColor.gray100,
                RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CustomText(
                text = meeting.title, // 모임명
                type = CustomTextType.titleMedium,
                color = CustomColor.black,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Column(
                modifier = Modifier.padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        4.dp
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = null,
                        tint = CustomColor.gray200,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(12.dp)
                    )
                    CustomText(
                        text = meeting.location ?: "설명 없음", // description을 location에 매핑
                        type = CustomTextType.bodyMedium,
                        color = CustomColor.gray200,
                        fontSize = 12.sp
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        4.dp
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_people),
                        contentDescription = null,
                        tint = CustomColor.gray200,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(12.dp)
                    )
                    CustomText(
                        text = meeting.peopleCounts.toString() + "명 참여",
                        type = CustomTextType.bodyMedium,
                        color = CustomColor.gray200,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

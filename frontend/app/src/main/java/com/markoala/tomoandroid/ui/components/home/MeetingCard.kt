package com.markoala.tomoandroid.ui.components.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor


data class MeetingSummary(
    val title: String,
    val location: String?,
    val time: String?,
    val peopleCounts: Int = 1
)

@Composable
fun MeetingCard(meeting: MeetingSummary) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CustomColor.white),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                CustomColor.gray100,
                androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )

    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
        ) {
            CustomText(
                text = meeting.title,
                type = CustomTextType.titleMedium,
                color = CustomColor.black,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Column(
                modifier = Modifier.padding(horizontal = 4.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                        4.dp
                    )
                ) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.markoala.tomoandroid.R.drawable.ic_location),
                        contentDescription = null,
                        tint = CustomColor.gray200,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(12.dp)
                    )
                    CustomText(
                        text = meeting.location ?: "",
                        type = CustomTextType.bodyMedium,
                        color = CustomColor.gray200,
                        fontSize = 12.sp
                    )
                }
                Row(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                        4.dp
                    )
                ) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.markoala.tomoandroid.R.drawable.ic_time),
                        contentDescription = null,
                        tint = CustomColor.gray200,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(12.dp)
                    )
                    CustomText(
                        text = meeting.time ?: "",
                        type = CustomTextType.bodyMedium,
                        color = CustomColor.gray200,
                        fontSize = 12.sp
                    )
                }
                Row(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                        4.dp
                    )
                ) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.markoala.tomoandroid.R.drawable.ic_people),
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
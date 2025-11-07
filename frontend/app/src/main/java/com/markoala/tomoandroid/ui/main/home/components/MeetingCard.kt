package com.markoala.tomoandroid.ui.main.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.data.model.moim.MoimList
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.main.home.HomeViewModel
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.util.parseIsoToKoreanDate

@Composable
fun MeetingCard(meeting: MoimList) {
    val homeViewModel: HomeViewModel = viewModel()
    val createdDate = parseIsoToKoreanDate(meeting.createdAt)

    Card(
        colors = CardDefaults.cardColors(containerColor = CustomColor.white),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CustomColor.gray100, RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomText(
                        text = meeting.title,
                        type = CustomTextType.title,
                        color = CustomColor.black,
                        fontSize = 16.sp
                    )
                    Column {
                        // 모임장 표시

                        CustomText(
                            text = if (meeting.leader) "모임장" else "팀원",
                            type = CustomTextType.body,
                            color = CustomColor.gray200,
                            fontSize = 12.sp
                        )


                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                CustomText(
                    text = meeting.description,
                    type = CustomTextType.title,
                    color = CustomColor.gray200,
                    fontSize = 12.sp
                )

                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (createdDate.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_time),
                                contentDescription = null,
                                tint = CustomColor.gray200,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(12.dp)
                            )
                            CustomText(
                                text = createdDate,
                                type = CustomTextType.body,
                                color = CustomColor.gray200,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_people),
                            contentDescription = null,
                            tint = CustomColor.gray200,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(12.dp)
                        )
                        CustomText(
                            text = meeting.peopleCount.toString() + "명",
                            type = CustomTextType.body,
                            color = CustomColor.gray200,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        homeViewModel.deleteMeeting(meeting.title)
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trash),
                    contentDescription = "모임 삭제",
                    tint = CustomColor.gray200,
                    modifier = Modifier.size(14.dp)
                )

                CustomText(
                    text = "모임삭제",
                    type = CustomTextType.body,
                    color = CustomColor.gray200,
                    fontSize = 12.sp
                )
            }
        }
    }
}

package com.markoala.tomoandroid.ui.main.home.meeting.steps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.data.model.friends.FriendProfile
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor


@Composable
fun StepThreeSection(
    moimName: String,
    description: String,
    selectedFriends: List<FriendProfile>
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CustomText(
            text = "입력한 내용을 확인하세요",
            type = CustomTextType.body,
            color = CustomColor.gray300
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = CustomColor.white),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, CustomColor.gray100),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomText(
                    text = "모임 정보 확인",
                    type = CustomTextType.title,
                    color = CustomColor.gray300
                )
                HorizontalDivider(color = CustomColor.gray100, thickness = 1.dp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    CustomText(
                        text = "제목: ",
                        type = CustomTextType.body,
                        color = CustomColor.gray300
                    )
                    CustomText(
                        text = moimName,
                        type = CustomTextType.title,
                        color = CustomColor.black,
                        fontSize = 14.sp
                    )

                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    CustomText(
                        text = "설명: ",
                        type = CustomTextType.body,
                        color = CustomColor.gray300
                    )
                    CustomText(
                        text = description,
                        type = CustomTextType.title,
                        color = CustomColor.black,
                        fontSize = 14.sp
                    )

                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    CustomText(
                        text = "인원 수: ",
                        type = CustomTextType.body,
                        color = CustomColor.gray300
                    )
                    CustomText(
                        text = selectedFriends.size.toString(),
                        type = CustomTextType.title,
                        color = CustomColor.black,
                        fontSize = 14.sp
                    )
                }
                // 추가된 친구 목록 표시
                if (selectedFriends.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CustomText(
                            text = "친구: ",
                            type = CustomTextType.body,
                            color = CustomColor.gray300
                        )
                        selectedFriends.forEachIndexed { index, friend ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                CustomText(
                                    text = friend.username + if (index != selectedFriends.lastIndex) "," else "",
                                    type = CustomTextType.title,
                                    color = CustomColor.black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                }

            }
        }


    }
}

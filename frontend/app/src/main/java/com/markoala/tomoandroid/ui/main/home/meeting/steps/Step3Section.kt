package com.markoala.tomoandroid.ui.main.home.meeting.steps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
            type = CustomTextType.bodyMedium,
            color = CustomColor.gray300
        )

        SummaryCard(title = "모임 제목", value = moimName)
        SummaryCard(title = "모임 설명", value = description)

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CustomColor.gray100),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                CustomText(
                    text = "초대 친구",
                    type = CustomTextType.titleMedium,
                    color = CustomColor.black
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (selectedFriends.isEmpty()) {
                    CustomText(
                        text = "선택된 친구가 없습니다.",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.gray200
                    )
                } else {
                    selectedFriends.forEachIndexed { index, friend ->
                        CustomText(
                            text = "${index + 1}. ${friend.username} (${friend.email})",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.black
                        )
                        if (index != selectedFriends.lastIndex) {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(title: String, value: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CustomColor.gray100),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomText(
                text = title,
                type = CustomTextType.bodySmall,
                color = CustomColor.gray200
            )
            CustomText(
                text = value,
                type = CustomTextType.bodyMedium,
                color = CustomColor.black
            )
        }
    }
}

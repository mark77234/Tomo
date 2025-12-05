package com.markoala.tomoandroid.ui.main.meeting.create_meeting.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.data.model.FriendProfile
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun StepThreeSection(
    title: String,
    description: String,
    selectedFriends: List<FriendProfile>
) {
    Surface(
        modifier = Modifier.fillMaxWidth().background(CustomColor.white),
        shape = RoundedCornerShape(24.dp),
        color = CustomColor.primary50
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomText(text = "입력한 내용을 확인하세요", type = CustomTextType.body, color = CustomColor.primaryDim)
            SummaryRow(label = "제목", value = title)
            SummaryRow(label = "설명", value = description)
            SummaryRow(label = "인원 수", value = selectedFriends.size.toString())
            if (selectedFriends.isNotEmpty()) {
                SummaryRow(
                    label = "친구",
                    value = selectedFriends.joinToString { it.username }
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        CustomText(text = label, type = CustomTextType.bodySmall, color = CustomColor.black)
        CustomText(text = value, type = CustomTextType.body, color = CustomColor.gray500)
    }
}

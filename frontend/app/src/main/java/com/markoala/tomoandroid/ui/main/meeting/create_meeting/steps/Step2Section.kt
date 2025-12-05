package com.markoala.tomoandroid.ui.main.meeting.create_meeting.steps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.data.model.FriendProfile
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.ProfileImage
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun StepTwoSection(
    friends: List<FriendProfile>,
    selectedEmails: Set<String>,
    onToggleEmail: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().background(CustomColor.white),
        shape = RoundedCornerShape(24.dp),
        color = CustomColor.primary50
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomText(text = "초대할 친구를 선택하세요", type = CustomTextType.title, color = CustomColor.primary, fontSize = 16.sp)
            if (friends.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth().background(CustomColor.white),
                    shape = RoundedCornerShape(16.dp),
                    color = CustomColor.white
                ) {
                    CustomText(
                        text = "초대할 친구가 없습니다.",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.textSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(friends, key = { it.email }) { friend ->
                        val selected = selectedEmails.contains(friend.email)
                        Surface(
                            modifier = Modifier.fillMaxWidth().background(CustomColor.primary50),
                            shape = RoundedCornerShape(18.dp),
                            color = if (selected) CustomColor.primary200 else CustomColor.white,
                            border = BorderStroke(1.dp, CustomColor.outline)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start=0.dp, top = 16.dp, bottom = 16.dp,end=12.dp),
                                verticalAlignment = Alignment.CenterVertically,

                            ) {
                                Checkbox(
                                    checked = selected,
                                    onCheckedChange = { onToggleEmail(friend.email) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = CustomColor.primary,
                                        uncheckedColor = CustomColor.outline,
                                        checkmarkColor = CustomColor.white
                                    )
                                )
                                ProfileImage(size = 48.dp)
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp) ) {
                                    CustomText(text = friend.username, type = CustomTextType.body, color = if (selected) CustomColor.primaryDim else CustomColor.black,)
                                    CustomText(text = friend.email, type = CustomTextType.bodySmall, color = if (selected) CustomColor.primaryDim else CustomColor.gray500,)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

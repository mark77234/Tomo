package com.markoala.tomoandroid.ui.main.home.meeting.steps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.data.model.friends.FriendProfile
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
    Column(modifier = Modifier.fillMaxWidth()) {
        CustomText(
            text = "초대할 친구를 선택하세요",
            type = CustomTextType.body,
            color = CustomColor.gray300
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (friends.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CustomColor.gray30
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CustomText(
                        text = "초대할 친구가 없습니다.",
                        type = CustomTextType.body,
                        color = CustomColor.gray200
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .border(
                        BorderStroke(1.dp, CustomColor.gray100),
                        shape = RoundedCornerShape(12.dp)
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(friends, key = { it.email }) { friend ->
                    val selected = selectedEmails.contains(friend.email)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleEmail(friend.email) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) CustomColor.gray30 else Color.White,

                        ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selected,
                                onCheckedChange = { onToggleEmail(friend.email) },
                                modifier = Modifier
                                    .padding(start = 6.dp, end = 16.dp)
                                    .size(20.dp), // 체크박스 크기 축소
                                colors = CheckboxDefaults.colors(
                                    checkedColor = CustomColor.gray300, // 체크된 상태의 색상
                                    uncheckedColor = CustomColor.gray100, // 체크 안 된 상태의 색상
                                    checkmarkColor = CustomColor.white // 체크 표시 색상
                                ),

                                )
                            ProfileImage(size = 48.dp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                CustomText(
                                    text = friend.username,
                                    type = CustomTextType.body,
                                    color = CustomColor.black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                CustomText(
                                    text = friend.email,
                                    type = CustomTextType.body,
                                    color = CustomColor.gray200
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = CustomColor.gray50,
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}
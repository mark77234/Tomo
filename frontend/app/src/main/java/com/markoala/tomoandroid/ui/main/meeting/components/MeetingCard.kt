package com.markoala.tomoandroid.ui.main.meeting.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.data.model.moim.MoimList
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.main.meeting.MeetingViewModel
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.util.parseIsoToKoreanDate

@Composable
fun MeetingCard(
    meeting: MoimList,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val homeViewModel: MeetingViewModel = viewModel()
    val createdDate = parseIsoToKoreanDate(meeting.createdAt)

    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = CustomColor.surface,
        border = BorderStroke(1.dp, CustomColor.outline)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                    CustomText(
                        text = meeting.title,
                        type = CustomTextType.title,
                        color = CustomColor.textPrimary
                    )
                    CustomText(
                        text = meeting.description,
                        type = CustomTextType.bodySmall,
                        color = CustomColor.textSecondary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (meeting.leader) CustomColor.primary.copy(alpha = 0.12f) else CustomColor.secondary.copy(alpha = 0.12f)
                ) {
                    CustomText(
                        text = if (meeting.leader) "모임장" else "팀원",
                        type = CustomTextType.bodySmall,
                        color = if (meeting.leader) CustomColor.primary else CustomColor.secondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MetaRow(icon = R.drawable.ic_time, text = createdDate)
                MetaRow(icon = R.drawable.ic_people, text = "${meeting.peopleCount}명 참여")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clickable(onClick = { homeViewModel.deleteMeeting(meeting.title) }),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_trash),
                        contentDescription = "모임 삭제",
                        tint = CustomColor.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    CustomText(
                        text = "모임 삭제",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun MetaRow(icon: Int, text: String) {
    if (text.isBlank()) return
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = CustomColor.textSecondary,
            modifier = Modifier.size(14.dp)
        )
        CustomText(
            text = text,
            type = CustomTextType.bodySmall,
            color = CustomColor.textSecondary
        )
    }
}

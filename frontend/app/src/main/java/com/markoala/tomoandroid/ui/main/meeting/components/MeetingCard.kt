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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.data.model.MoimListDTO
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.DangerDialog
import com.markoala.tomoandroid.ui.main.meeting.MeetingViewModel
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.utils.getFriendshipDurationText
import com.markoala.tomoandroid.utils.parseIsoToKoreanDate

@Composable
fun MeetingCard(
    meeting: MoimListDTO,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val homeViewModel: MeetingViewModel = viewModel()
    val createdDate = parseIsoToKoreanDate(meeting.createdAt)
    val friendshipDuration = getFriendshipDurationText(meeting.createdAt ?: "")
    var showDeleteDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = CustomColor.primary50,
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
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
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
            HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            color = CustomColor.outline,
                            thickness = 1.dp
                        )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MetaRow( text = "$friendshipDuration 동안 함께하고 있어요")
                MetaRow(icon = R.drawable.ic_time,text = "최초생성일: $createdDate")

                MetaRow(icon = R.drawable.ic_people, text = "${meeting.peopleCount}명 참여 중")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (meeting.leader) {
                    Row(
                        modifier = Modifier
                            .clickable(onClick = { showDeleteDialog = true }),
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
            if (showDeleteDialog) {
                DangerDialog(
                    title = "모임 삭제",
                    message = "정말로 '${meeting.title}' 모임을 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.",
                    confirmText = "삭제",
                    dismissText = "취소",
                    onConfirm = {
                        homeViewModel.deleteMeeting(meeting.moimId)
                        showDeleteDialog = false
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }
        }
    }
}

@Composable
private fun MetaRow(icon: Int? = null, text: String) {
    if (text.isBlank()) return
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (icon != null) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = CustomColor.textSecondary,
                modifier = Modifier.size(14.dp)
            )
        }
        CustomText(
            text = text,
            type = CustomTextType.bodySmall,
            color = CustomColor.textSecondary
        )
    }
}

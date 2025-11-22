package com.markoala.tomoandroid.ui.main.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor


private data class OverviewAction(
    val title: String,
    val description: String,
    val icon: Any,
    val onClick: () -> Unit,
)
@Composable
fun NavigationSection(
    onAddFriendsClick: () -> Unit,
    onAffinityTabClick: () -> Unit,
    onMeetingClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    val actions = listOf(
        OverviewAction(
            title = "친구 추가",
            description = "코드 공유나 검색으로 친구를 초대해요",
            icon = painterResource(id =com.markoala.tomoandroid.R.drawable.ic_addfriend),
            onClick = onAddFriendsClick
        ),
        OverviewAction(
            title = "친밀도 탭",
            description = "친구들과의 스토리와 레벨을 확인해요",
            icon = Icons.Filled.Favorite,
            onClick = onAffinityTabClick
        ),
        OverviewAction(
            title = "모임",
            description = "모임을 만들고 친구들과 함께해요",
            icon = painterResource(id =com.markoala.tomoandroid.R.drawable.ic_friends),
            onClick = onMeetingClick
        ),
        OverviewAction(
            title = "내 정보",
            description = "프로필과 기본 정보를 확인해요",
            icon = Icons.Filled.Person,
            onClick = onProfileClick
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CustomText(
            text = "토모 허브",
            type = CustomTextType.title,
            color = CustomColor.textPrimary
        )
        CustomText(
            text = "필요한 탭으로 바로 이동해 보세요",
            type = CustomTextType.bodySmall,
            color = CustomColor.textSecondary
        )
        actions.chunked(2).forEach { rowActions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowActions.forEach { action ->
                    ActionCard(
                        action = action,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowActions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ActionCard(action: OverviewAction, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .heightIn(min = 140.dp)
            .border(
                width = 1.dp,
                color = CustomColor.gray200,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { action.onClick() },
        shape = RoundedCornerShape(24.dp),
        color = CustomColor.primary50,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = CustomColor.primary.copy(alpha = 0.12f)
            ) {
                // ImageVector or Painter 둘 다 처리
                when (val ic = action.icon) {
                    is ImageVector -> Icon(
                        imageVector = ic,
                        contentDescription = action.title,
                        tint = CustomColor.primary,
                        modifier = Modifier.padding(10.dp)
                    )

                    is Painter -> Icon(
                        painter = ic,
                        contentDescription = action.title,
                        tint = CustomColor.primary,
                        modifier = Modifier.padding(10.dp)
                    )

                    else -> {
                        // Fallback: 빈 공간
                        Spacer(modifier = Modifier.height(0.dp))
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CustomText(
                    text = action.title,
                    type = CustomTextType.title,
                    color = CustomColor.textPrimary
                )
                CustomText(
                    text = action.description,
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )
            }
        }
    }
}


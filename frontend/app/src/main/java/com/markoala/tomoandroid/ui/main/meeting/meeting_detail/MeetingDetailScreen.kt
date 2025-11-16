package com.markoala.tomoandroid.ui.main.meeting.meeting_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.data.model.friends.FriendProfile
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.main.friends.components.FriendCard
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.util.parseIsoToKoreanDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth
import com.markoala.tomoandroid.ui.components.CustomBack
import com.markoala.tomoandroid.util.getFriendshipDurationText

@Composable
fun MeetingDetailScreen(
    moimId: Int,
    onBackClick: () -> Unit,
    viewModel: MeetingDetailViewModel = viewModel()
) {
    val moimDetails by viewModel.moimDetails.collectAsState()
    val membersWithProfiles by viewModel.membersWithProfiles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(moimId) {
        viewModel.fetchMoimDetails(moimId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(bottom = 30.dp) // 최하단 바텀 여백 추가
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CustomColor.primary)
                }
            }

            errorMessage != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CustomText(
                        text = "오류가 발생했습니다",
                        type = CustomTextType.title,
                        color = CustomColor.textPrimary
                    )
                    CustomText(
                        text = errorMessage ?: "",
                        type = CustomTextType.body,
                        color = CustomColor.textSecondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            moimDetails != null -> {
                MeetingDetailContent(
                    moimDetails = moimDetails!!,
                    membersWithProfiles = membersWithProfiles,
                    onBackClick = onBackClick
                )
            }

        }
    }
}

@Composable
private fun MeetingDetailContent(
    moimDetails: com.markoala.tomoandroid.data.model.moim.MoimDetails,
    membersWithProfiles: List<MemberWithProfile>,
    onBackClick: () -> Unit
) {
    val createdDate = parseIsoToKoreanDate(moimDetails.createdAt)
    val daysActive = getFriendshipDurationText(moimDetails.createdAt)
    val averageFriendship = calculateAverageFriendship(membersWithProfiles)
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 뒤로가기 버튼
        item {
            CustomBack(
                onClick = onBackClick,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // 모임 정보 Hero 카드
        item {
            Surface(
                modifier = Modifier.shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = CustomColor.primary.copy(alpha = 0.1f),
                    ambientColor = CustomColor.primary.copy(alpha = 0.05f)
                ),
                shape = RoundedCornerShape(28.dp),
                color = CustomColor.primaryContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 제목과 설명
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomText(
                            text = moimDetails.title,
                            type = CustomTextType.display,
                            color = CustomColor.primary
                        )
                        CustomText(
                            text = moimDetails.description,
                            type = CustomTextType.body,
                            color = CustomColor.primaryDim
                        )
                    }

                    // 날짜 및 통계 정보
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 생성일
                        InfoRow(
                            icon = R.drawable.ic_time,
                            label = "생성일",
                            value = createdDate
                        )

                        // 유지 일수
                        InfoRow(
                            icon = R.drawable.ic_time,
                            label = "유지 일수",
                            value = "${daysActive}째 진행 중"
                        )
                    }
                }
            }
        }

        // 통계 카드
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 멤버 수
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "멤버",
                    value = "${moimDetails.members.size}명",
                    icon = R.drawable.ic_people
                )

                // 평균 친밀도
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "평균 친밀도",
                    value = String.format(Locale.getDefault(), "%d점", averageFriendship.toInt()),
                    icon = R.drawable.ic_favorite
                )
            }
        }

        // 멤버 섹션 헤더
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                CustomText(
                    text = "모임 멤버",
                    type = CustomTextType.title,
                    color = CustomColor.textPrimary
                )
                CustomText(
                    text = "함께하는 친구들의 상세 정보",
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )
            }
        }

        // 멤버 리스트
        items(membersWithProfiles) { memberWithProfile ->
            val profile = memberWithProfile.profile
            val isCurrentUser = memberWithProfile.email == currentUserEmail

            // 모든 멤버 표시 (프로필이 있는 경우에만)
            if (profile != null) {
                FriendCard(
                    friend = FriendProfile(
                        username = profile.username,
                        email = memberWithProfile.email,
                        friendship = profile.friendship,
                        createdAt = profile.createdAt
                    ),
                    isLeader = memberWithProfile.leader,
                    showDeleteButton = false,
                    isCurrentUser = isCurrentUser
                )
            }
        }

        // 하단 여백 추가
        item {
            Spacer(modifier = Modifier.height(30.dp))
        }

    }
}

@Composable
private fun InfoRow(
    icon: Int,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = CustomColor.primary.copy(alpha = 0.15f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = CustomColor.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            CustomText(
                text = label,
                type = CustomTextType.bodySmall,
                color = CustomColor.primaryDim.copy(alpha = 0.7f)
            )
            CustomText(
                text = value,
                type = CustomTextType.body,
                color = CustomColor.primaryDim
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: Int
) {
    Surface(
        modifier = modifier.shadow(
            elevation = 1.dp,
            shape = RoundedCornerShape(20.dp),
            spotColor = CustomColor.primary.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(20.dp),
        color = CustomColor.white
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = CustomColor.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = CustomColor.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CustomText(
                    text = value,
                    type = CustomTextType.title,
                    color = CustomColor.textPrimary
                )
                CustomText(
                    text = label,
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )
            }
        }
    }
}

private fun calculateAverageFriendship(members: List<MemberWithProfile>): Double {
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
    val friendships = members
        .filter { it.email != currentUserEmail } // 본인 제외
        .mapNotNull { it.profile?.friendship }
        .map { it.toDouble() }
    return if (friendships.isNotEmpty()) {
        friendships.average()
    } else {
        0.0
    }
}

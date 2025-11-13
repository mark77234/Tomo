package com.markoala.tomoandroid.ui.main.meeting.meeting_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.data.model.moim.Member
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.util.parseIsoToKoreanDate

@Composable
fun MeetingDetailScreen(
    moimTitle: String,
    onBackClick: () -> Unit,
    viewModel: MeetingDetailViewModel = viewModel()
) {
    val moimDetails by viewModel.moimDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(moimTitle) {
        viewModel.fetchMoimDetails(moimTitle)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.background)
            .padding(top = androidx.compose.foundation.layout.WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 8.dp)
    ) {
        // 상단 헤더
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CustomColor.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = CustomColor.textPrimary
                    )
                }
                CustomText(
                    text = "모임 상세",
                    type = CustomTextType.title,
                    color = CustomColor.textPrimary
                )
                Box(modifier = Modifier.size(48.dp))
            }
        }

        // 컨텐츠
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomText(
                            text = "오류가 발생했습니다",
                            type = CustomTextType.title,
                            color = CustomColor.textPrimary
                        )
                        CustomText(
                            text = errorMessage ?: "",
                            type = CustomTextType.body,
                            color = CustomColor.textSecondary
                        )
                    }
                }
            }

            moimDetails != null -> {
                MeetingDetailContent(moimDetails = moimDetails!!)
            }
        }
    }
}

@Composable
private fun MeetingDetailContent(moimDetails: com.markoala.tomoandroid.data.model.moim.MoimDetails) {
    val createdDate = parseIsoToKoreanDate(moimDetails.createdAt)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 모임 정보 섹션
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CustomColor.surface
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomText(
                        text = moimDetails.title,
                        type = CustomTextType.display,
                        color = CustomColor.textPrimary
                    )
                    CustomText(
                        text = moimDetails.description,
                        type = CustomTextType.body,
                        color = CustomColor.textSecondary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_time),
                            contentDescription = null,
                            tint = CustomColor.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        CustomText(
                            text = "생성일: $createdDate",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.textSecondary
                        )
                    }
                }
            }
        }

        // 멤버 섹션 헤더
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CustomText(
                    text = "모임 멤버",
                    type = CustomTextType.title,
                    color = CustomColor.textPrimary
                )
                CustomText(
                    text = "${moimDetails.members.size}명의 멤버",
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )
            }
        }

        // 멤버 리스트
        items(moimDetails.members) { member ->
            MemberCard(member = member)
        }
    }
}

@Composable
private fun MemberCard(member: Member) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CustomColor.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 아바타
                Surface(
                    shape = CircleShape,
                    color = if (member.leader) CustomColor.primary.copy(alpha = 0.2f) else CustomColor.secondary.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CustomText(
                            text = member.username.firstOrNull()?.toString() ?: "?",
                            type = CustomTextType.title,
                            color = if (member.leader) CustomColor.primary else CustomColor.secondary
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    CustomText(
                        text = member.username,
                        type = CustomTextType.body,
                        color = CustomColor.textPrimary
                    )
                    CustomText(
                        text = member.email,
                        type = CustomTextType.bodySmall,
                        color = CustomColor.textSecondary
                    )
                }
            }

            if (member.leader) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CustomColor.primary.copy(alpha = 0.12f)
                ) {
                    CustomText(
                        text = "모임장",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

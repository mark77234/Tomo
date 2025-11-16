package com.markoala.tomoandroid.ui.main.meeting.meeting_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.draw.shadow
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
    moimId: Int,
    onBackClick: () -> Unit,
    viewModel: MeetingDetailViewModel = viewModel()
) {
    val moimDetails by viewModel.moimDetails.collectAsState()
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
                    onBackClick = onBackClick
                )
            }
        }
    }
}

@Composable
private fun MeetingDetailContent(
    moimDetails: com.markoala.tomoandroid.data.model.moim.MoimDetails,
    onBackClick: () -> Unit
) {
    val createdDate = parseIsoToKoreanDate(moimDetails.createdAt)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 뒤로가기 버튼
        item {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = CustomColor.textPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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

                    // 날짜 정보
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CustomColor.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_time),
                                    contentDescription = null,
                                    tint = CustomColor.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        CustomText(
                            text = createdDate,
                            type = CustomTextType.body,
                            color = CustomColor.primaryDim
                        )
                    }
                }
            }
        }

        // 멤버 섹션 헤더
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 12.dp)
            ) {
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
        modifier = Modifier.shadow(
            elevation = 1.dp,
            shape = RoundedCornerShape(20.dp),
            spotColor = CustomColor.gray900.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(20.dp),
        color = CustomColor.white
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
//                Surface(
//                    shape = CircleShape,
//                    color = if (member.leader) {
//                        CustomColor.primaryContainer
//                    } else {
//                        CustomColor.gray100
//                    },
//                    modifier = Modifier.size(48.dp)
//                ) {
//                    Box(contentAlignment = Alignment.Center) {
//                        CustomText(
//                            text = member.username.firstOrNull()?.uppercase() ?: "?",
//                            type = CustomTextType.title,
//                            color = if (member.leader) {
//                                CustomColor.primary
//                            } else {
//                                CustomColor.textSecondary
//                            }
//                        )
//                    }
//                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
//                        CustomText(
//                            text = member.username,
//                            type = CustomTextType.body,
//                            color = CustomColor.textPrimary
//                        )
                        if (member.leader) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CustomColor.primary
                            ) {
                                CustomText(
                                    text = "모임장",
                                    type = CustomTextType.bodySmall,
                                    color = CustomColor.white,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                    CustomText(
                        text = member.email,
                        type = CustomTextType.bodySmall,
                        color = CustomColor.textSecondary
                    )
                }
            }
        }
    }
}

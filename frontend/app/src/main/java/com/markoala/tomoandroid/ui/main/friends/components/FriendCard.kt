package com.markoala.tomoandroid.ui.main.friends.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.auth.AuthManager.getStoredAccessToken
import com.markoala.tomoandroid.data.api.friendsApi
import com.markoala.tomoandroid.data.model.FriendProfile
import com.markoala.tomoandroid.data.model.FriendSummary
import com.markoala.tomoandroid.data.model.BaseResponse
import com.markoala.tomoandroid.data.repository.friends.FriendsRepository
import com.markoala.tomoandroid.ui.components.CustomDialog
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.utils.getFriendshipDurationText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun FriendCard(
    friend: FriendProfile,
    isLeader: Boolean = false,
    showDeleteButton: Boolean = true,
    isCurrentUser: Boolean = false,
    onFriendDeleted: (() -> Unit)? = null,
    onFriendAdded: (() -> Unit)? = null // 추가
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    val toastManager = LocalToastManager.current

    val friendsRepository = remember { FriendsRepository() }

    fun addFriend(email: String) {
        friendsRepository.postFriends(
            email = email,
            onLoading = { loading -> loading },
            onSuccess = {
                toastManager.showSuccess("친구가 성공적으로 추가되었습니다!")
                onFriendAdded?.invoke() // 성공 시 refetch 콜백 실행
            },
            onError = { error ->
                toastManager.showWarning(error)
            }
        )
    }

    Surface(
        modifier = Modifier
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = CustomColor.gray900.copy(alpha = 0.05f)
            )
            .border(
                width = 1.dp,
                color = CustomColor.gray100,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        color = CustomColor.white
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 아바타
                    Surface(
                        shape = CircleShape,
                        color = if (isLeader) {
                            CustomColor.primaryContainer
                        } else {
                            CustomColor.gray100
                        },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            CustomText(
                                text = friend.username.firstOrNull()?.uppercase() ?: "?",
                                type = CustomTextType.title,
                                color = if (isLeader) {
                                    CustomColor.primary
                                } else {
                                    CustomColor.textSecondary
                                }
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomText(
                                text = friend.username,
                                type = CustomTextType.body,
                                color = CustomColor.textPrimary
                            )
                            if (isLeader) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CustomColor.secondary
                                ) {
                                    CustomText(
                                        text = "모임장",
                                        type = CustomTextType.bodySmall,
                                        color = CustomColor.white,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            // 본인 뱃지
                            if (isCurrentUser) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CustomColor.primary.copy(alpha = 0.5f)
                                ) {
                                    CustomText(
                                        text = "본인",
                                        type = CustomTextType.bodySmall,
                                        color = CustomColor.white,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            // 친구 아님 뱃지 (본인이 아니고 친밀도가 0인 경우)
                            if (!isCurrentUser && friend.createdAt.isEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CustomColor.textSecondary.copy(alpha = 0.2f)
                                ) {
                                    CustomText(
                                        text = "친구아님",
                                        type = CustomTextType.bodySmall,
                                        color = CustomColor.textSecondary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                            }
                        }
                        CustomText(
                            text = friend.email,
                            type = CustomTextType.bodySmall,
                            color = CustomColor.textSecondary
                        )
                    }
                }
                if (!isCurrentUser) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CustomColor.white,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { showReportDialog = true }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.ic_notification),
                                contentDescription = "신고하기",
                                tint = CustomColor.danger,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // 친밀도 정보 (본인이 아니고 친구인 경우만 표시)
            if (!isCurrentUser && friend.friendship > -1 && friend.createdAt.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 친밀도 점수 카드
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = CustomColor.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = CustomColor.primary.copy(alpha = 0.2f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_favorite),
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
                                        text = "친밀도 점수",
                                        type = CustomTextType.bodySmall,
                                        color = CustomColor.primaryDim
                                    )
                                    CustomText(
                                        text = "${friend.friendship} 점",
                                        type = CustomTextType.title,
                                        color = CustomColor.primary
                                    )
                                }
                            }

                            // 친밀도 레벨 표시
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = getFriendshipLevelColor(friend.friendship)
                            ) {
                                CustomText(
                                    text = getFriendshipLevel(friend.friendship),
                                    type = CustomTextType.bodySmall,
                                    color = CustomColor.white,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // 친구 된 날짜
                    if (friend.createdAt.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_time),
                                contentDescription = null,
                                tint = CustomColor.textSecondary.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                            CustomText(
                                text = " ${getFriendshipDurationText(friend.createdAt)} 째 친구 ",
                                type = CustomTextType.bodySmall,
                                color = CustomColor.textSecondary
                            )
                        }
                    }
                }
            }

            // 친구 손절 버튼
            if (showDeleteButton) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        modifier = Modifier.clickable { showDeleteDialog = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_trash),
                            contentDescription = "친구 손절",
                            tint = CustomColor.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        CustomText(
                            text = "친구 손절",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.textSecondary
                        )
                    }
                }
            }
            if (!isCurrentUser && friend.createdAt.isEmpty()) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CustomColor.primary.copy(alpha = 0.8f),
                        modifier = Modifier.clickable { addFriend(friend.email) }
                    ) {
                        CustomText(
                            text = "친구추가",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.white,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )

                    }
                }
            }

        }
    }

    if (showReportDialog) {
        CustomDialog(
            title = "신고하기",
            message = "${friend.username}님을 신고하시겠어요?\n부적절한 활동을 알려주세요.",
            confirmText = "신고하기",
            dismissText = "취소",
            onConfirm = {
                toastManager.showSuccess("신고되었습니다.")
                showReportDialog = false
            },
            onDismiss = { showReportDialog = false }
        )
    }

    if (showDeleteDialog) {
        DeleteFriendDialog(
            friendName = friend.username,
            onConfirm = {
                val accessToken = getStoredAccessToken()
                Log.d("FriendCard", "삭제 요청 – 토큰 존재 여부: ${accessToken != null}")

                friendsApi.deleteFriends(friend.email)
                    .enqueue(object : Callback<BaseResponse<FriendSummary>> {
                        override fun onResponse(
                            call: Call<BaseResponse<FriendSummary>>,
                            response: Response<BaseResponse<FriendSummary>>
                        ) {
                            if (response.isSuccessful) {
                                toastManager.showSuccess("${friend.username}님이 친구 목록에서 삭제되었습니다.")
                                onFriendDeleted?.invoke()
                            } else {
                                Log.e("FriendCard", "친구삭제 실패: ${response.code()} ${response.message()}")
                                toastManager.showError("친구삭제에 실패했습니다.")
                            }
                        }

                        override fun onFailure(
                            call: Call<BaseResponse<FriendSummary>>,
                            t: Throwable
                        ) {
                            Log.e("FriendCard", "친구삭제 네트워크 오류: ${t.message}", t)
                            toastManager.showError("네트워크 오류가 발생했습니다.")
                        }
                    })
                showDeleteDialog = false
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
}

// 친밀도 점수에 따른 레벨 반환
private fun getFriendshipLevel(score: Int): String {
    return when {
        score >= 100 -> "최고의 친구"
        score >= 50 -> "절친"
        score >= 20 -> "좋은 친구"
        score >= 10 -> "친구"
        score >= 5 -> "아는 사이"
        else -> "어색한 친구"
    }
}

// 친밀도 레벨에 따른 색상 반환
private fun getFriendshipLevelColor(score: Int): Color {
    return when {
        score >= 100 -> Color(0xFFD97A7A) // 따뜻한 로즈 (최고의 친구)
        score >= 50 -> Color(0xFFE89A67) // 피치 코랄 (절친)
        score >= 20 -> Color(0xFFD9B559) // 허니 머스터드 (좋은 친구)
        score >= 10 -> Color(0xFFA6C48A) // 소프트 올리브 그린 (친구)
        score >= 5 -> Color(0xFF9FBFAD) // 세이지 미스트 (아는 사이)
        else -> Color(0xFFC9C5C1) // 웜 라이트 그레이 (새로운 친구)
    }
}

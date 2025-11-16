package com.markoala.tomoandroid.ui.main.friends.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.auth.AuthManager.getStoredAccessToken
import com.markoala.tomoandroid.data.api.friendsApi
import com.markoala.tomoandroid.data.model.friends.FriendProfile
import com.markoala.tomoandroid.data.model.friends.FriendSummary
import com.markoala.tomoandroid.data.model.user.BaseResponse
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.components.ProfileImage
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.util.getFriendshipDurationText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun FriendCard(
    friend: FriendProfile,
    onFriendDeleted: (() -> Unit)? = null
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val toastManager = LocalToastManager.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CustomColor.surface,
        border = BorderStroke(1.dp, CustomColor.outline)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ProfileImage(size = 56.dp)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    CustomText(text = friend.username, type = CustomTextType.title, color = CustomColor.textPrimary)
                    CustomText(text = friend.email, type = CustomTextType.bodySmall, color = CustomColor.textSecondary)
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CustomColor.secondary.copy(alpha = 0.15f)
                ) {
                    CustomText(
                        text = "친밀도 ${friend.friendship}",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.secondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                MetaInfo(icon = R.drawable.ic_time, text = "우정 기간: ${getFriendshipDurationText(friend.createdAt)}")
            }

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

@Composable
private fun MetaInfo(icon: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = CustomColor.textSecondary,
            modifier = Modifier.size(14.dp)
        )
        CustomText(text = text, type = CustomTextType.bodySmall, color = CustomColor.textSecondary)
    }
}

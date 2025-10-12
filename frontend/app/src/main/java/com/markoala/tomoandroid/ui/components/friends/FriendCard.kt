package com.markoala.tomoandroid.ui.components.friends

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.data.api.apiService
import com.markoala.tomoandroid.data.model.FriendProfile
import com.markoala.tomoandroid.data.model.FriendsResponseDTO
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.components.ProfileImage
import com.markoala.tomoandroid.ui.theme.CustomColor
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

    Card(
        colors = CardDefaults.cardColors(containerColor = CustomColor.white),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                CustomColor.gray100,
                androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            ) {
                ProfileImage(
                    modifier = Modifier.padding(end = 10.dp),
                    size = 50.dp,
                    imageUrl = null // 기본 아이콘 표시
                )
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    CustomText(
                        text = friend.username,
                        type = CustomTextType.titleMedium,
                        color = CustomColor.black,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                            4.dp
                        )
                    ) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.markoala.tomoandroid.R.drawable.ic_email),
                            contentDescription = null,
                            tint = CustomColor.gray200,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(12.dp)
                        )
                        CustomText(
                            text = friend.email,
                            type = CustomTextType.bodyMedium,
                            color = CustomColor.gray200,
                            fontSize = 12.sp
                        )
                    }
                }
                CustomText(
                    text = "친밀도: " + friend.friendship.toString(),
                    type = CustomTextType.bodyMedium,
                    color = CustomColor.gray200,
                    fontSize = 12.sp
                )

            }
            Column(
                modifier = Modifier.padding(horizontal = 4.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
            ) {

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = CustomColor.gray50,
                    thickness = 1.dp
                )
                Row(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                        4.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.markoala.tomoandroid.R.drawable.ic_time),
                        contentDescription = null,
                        tint = CustomColor.gray200,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(12.dp)
                    )
                    CustomText(
                        text = "우정 기간: " + friend.createdAt,
                        type = CustomTextType.bodyMedium,
                        color = CustomColor.gray200,
                        fontSize = 12.sp
                    )
                }

                // 친구삭제 버튼
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CustomColor.white,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CustomColor.gray100),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {

                    CustomText(
                        text = "친구삭제",
                        type = CustomTextType.titleMedium,
                        fontSize = 14.sp,
                        color = CustomColor.gray300
                    )
                }
            }

        }
    }

    // 친구삭제 확인 다이얼로그
    if (showDeleteDialog) {
        DeleteFriendDialog(
            friendName = friend.username,
            onConfirm = {
                // API 호출로 친구 삭제
                android.util.Log.d("FriendCard", "=== 친구삭제 API 호출 시작 ===")
                android.util.Log.d("FriendCard", "삭제할 친구 이메일: ${friend.email}")
                android.util.Log.d(
                    "FriendCard",
                    "요청 URL: DELETE /public/friends?email=${friend.email}"
                )

                // 현재 저장된 액세스 토큰 확인
                val accessToken = com.markoala.tomoandroid.auth.AuthManager.getStoredAccessToken()
                android.util.Log.d("FriendCard", "현재 액세스 토큰 존재 여부: ${accessToken != null}")
                if (accessToken != null) {
                    android.util.Log.d("FriendCard", "액세스 토큰 앞 10자리: ${accessToken.take(10)}...")
                }

                apiService.deleteFriends(friend.email)
                    .enqueue(object : Callback<FriendsResponseDTO> {
                        override fun onResponse(
                            call: Call<FriendsResponseDTO>,
                            response: Response<FriendsResponseDTO>
                        ) {
                            android.util.Log.d("FriendCard", "=== API 응답 수신 ===")
                            android.util.Log.d("FriendCard", "Response Code: ${response.code()}")
                            android.util.Log.d(
                                "FriendCard",
                                "Response Message: ${response.message()}"
                            )
                            android.util.Log.d("FriendCard", "Request URL: ${call.request().url}")
                            android.util.Log.d(
                                "FriendCard",
                                "Request Headers: ${call.request().headers}"
                            )

                            if (response.isSuccessful) {
                                val responseBody = response.body()
                                android.util.Log.d("FriendCard", "친구삭제 성공!")
                                android.util.Log.d("FriendCard", "응답 본문: $responseBody")
                                toastManager.showSuccess("${friend.username}님이 친구 목록에서 삭제되었습니다.")
                                onFriendDeleted?.invoke()
                            } else {
                                val errorBody = response.errorBody()?.string()
                                android.util.Log.e("FriendCard", "=== 친구삭제 실패 ===")
                                android.util.Log.e("FriendCard", "오류 코드: ${response.code()}")
                                android.util.Log.e("FriendCard", "오류 메시지: ${response.message()}")
                                android.util.Log.e("FriendCard", "오류 본문: $errorBody")
                                android.util.Log.e("FriendCard", "응답 헤더: ${response.headers()}")

                                // 400 에러인 경우 추가 정보 로깅
                                if (response.code() == 400) {
                                    android.util.Log.e("FriendCard", "400 Bad Request - 가능한 원인:")
                                    android.util.Log.e("FriendCard", "1. 인증 토큰 문제")
                                    android.util.Log.e("FriendCard", "2. 요청 형식 문제")
                                    android.util.Log.e("FriendCard", "3. 친구 관계가 존재하지 않음")
                                    android.util.Log.e("FriendCard", "4. 권한 부족")
                                }
                                toastManager.showError("친구삭제에 실패했습니다.")
                            }
                        }

                        override fun onFailure(call: Call<FriendsResponseDTO>, t: Throwable) {
                            android.util.Log.e("FriendCard", "친구삭제 네트워크 오류: ${t.message}", t)
                            toastManager.showError("네트워크 오류가 발생했습니다.")
                        }
                    })
                showDeleteDialog = false
            },
            onDismiss = {
                android.util.Log.d("FriendCard", "친구삭제 다이얼로그 취소됨")
                showDeleteDialog = false
            }
        )
    }
}
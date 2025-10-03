package com.markoala.tomoandroid.ui.main.friends

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.data.api.apiService
import com.markoala.tomoandroid.data.model.FriendSearchRequest
import com.markoala.tomoandroid.data.model.FriendSearchResponse
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.DashedBorderBox
import com.markoala.tomoandroid.ui.theme.CustomColor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AddFriendsScreen(
    paddingValues: PaddingValues,
    onBackClick: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<String?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // 친구 검색 함수
    fun searchFriend(email: String) {
        Log.d("AddFriendsScreen", "searchFriend 시작 - 입력된 이메일: $email")

        if (email.isBlank()) {
            Log.w("AddFriendsScreen", "이메일이 비어있음")
            Toast.makeText(context, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        isSearching = true
        searchResult = null
        Log.d("AddFriendsScreen", "검색 상태 변경: isSearching = true")

        // Firebase ID 토큰 가져오기
        Log.d("AddFriendsScreen", "Firebase 토큰 요청 시작")
        AuthManager.auth.currentUser?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
            if (tokenTask.isSuccessful) {
                val firebaseToken = tokenTask.result?.token
                Log.d("AddFriendsScreen", "Firebase 토큰 획득 성공")
                Log.d("AddFriendsScreen", "토큰 길이: ${firebaseToken?.length ?: 0}")
                Log.d("AddFriendsScreen", "토큰 앞 20자: ${firebaseToken?.take(20) ?: "null"}...")

                if (firebaseToken != null) {
                    // API 요청
                    val request = FriendSearchRequest(email = email)
                    Log.d("AddFriendsScreen", "API 요청 생성 - 요청 데이터: $request")
                    Log.d(
                        "AddFriendsScreen",
                        "Authorization 헤더: Bearer ${firebaseToken.take(20)}..."
                    )

                    val call = apiService.searchFriend("Bearer $firebaseToken", request)
                    Log.d("AddFriendsScreen", "API 호출 시작 - URL: ${call.request().url}")
                    Log.d("AddFriendsScreen", "HTTP 메소드: ${call.request().method}")

                    call.enqueue(object : Callback<FriendSearchResponse> {
                        override fun onResponse(
                            call: Call<FriendSearchResponse>,
                            response: Response<FriendSearchResponse>
                        ) {
                            Log.d("AddFriendsScreen", "API 응답 수신")
                            Log.d("AddFriendsScreen", "응답 코드: ${response.code()}")
                            Log.d("AddFriendsScreen", "응답 메시지: ${response.message()}")
                            Log.d("AddFriendsScreen", "응답 성공 여부: ${response.isSuccessful}")

                            isSearching = false
                            if (response.isSuccessful) {
                                val result = response.body()
                                Log.d("AddFriendsScreen", "응답 본문: $result")

                                if (result?.success == true && result.data != null) {
                                    Log.d(
                                        "AddFriendsScreen",
                                        "친구 검색 성공 - 사용자명: ${result.data.username}, 이메일: ${result.data.email}"
                                    )
                                    searchResult = "${result.data.username} (${result.data.email})"
                                    Toast.makeText(context, "친구를 찾았습니다!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Log.w(
                                        "AddFriendsScreen",
                                        "친구 검색 실패 - success: ${result?.success}, data: ${result?.data}, message: ${result?.message}"
                                    )
                                    searchResult = "친구를 찾을 수 없습니다"
                                    Toast.makeText(
                                        context,
                                        result?.message ?: "친구를 찾을 수 없습니다",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Log.e(
                                    "AddFriendsScreen",
                                    "HTTP 응답 실패 - 코드: ${response.code()}, 메시지: ${response.message()}"
                                )
                                Log.e(
                                    "AddFriendsScreen",
                                    "에러 본문: ${response.errorBody()?.string()}"
                                )
                                searchResult = "검색 실패"
                                Toast.makeText(context, "검색에 실패했습니다", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<FriendSearchResponse>, t: Throwable) {
                            Log.e("AddFriendsScreen", "API 요청 완전 실패", t)
                            Log.e("AddFriendsScreen", "실패 원인: ${t.javaClass.simpleName}")
                            Log.e("AddFriendsScreen", "에러 메시지: ${t.message}")
                            Log.e("AddFriendsScreen", "요청 URL: ${call.request().url}")

                            isSearching = false
                            searchResult = "네트워크 오류"
                            Toast.makeText(context, "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Log.e("AddFriendsScreen", "Firebase 토큰이 null")
                    isSearching = false
                    Toast.makeText(context, "인증 토큰을 가져올 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("AddFriendsScreen", "Firebase 토큰 획득 실패", tokenTask.exception)
                Log.e(
                    "AddFriendsScreen",
                    "토큰 태스크 예외: ${tokenTask.exception?.javaClass?.simpleName}"
                )
                Log.e("AddFriendsScreen", "토큰 태스크 메시지: ${tokenTask.exception?.message}")

                isSearching = false
                Toast.makeText(context, "인증에 실패했습니다", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Log.e("AddFriendsScreen", "현재 사용자가 null - 로그인되지 않음")
            isSearching = false
            Toast.makeText(context, "로그인이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // 상단 헤더
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomText(
                    text = "친구 추가",
                    type = CustomTextType.headlineLarge,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = CustomColor.gray100,
                            shape = RoundedCornerShape(32.dp)
                        )
                        .clickable { onBackClick() },
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White
                ) {
                    Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)) {
                        CustomText(
                            text = "목록보기",
                            type = CustomTextType.titleSmall,
                            fontSize = 14.sp,
                            color = CustomColor.black
                        )
                    }

                }
            }

        }


        Spacer(modifier = Modifier.height(16.dp))
        var selectedOption by remember { mutableStateOf("email") } // "phone" 또는 "email"

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    color = CustomColor.gray100,
                    width = 1.dp,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(color = CustomColor.white)
                .height(50.dp)
        ) {
            // 전화번호 선택 영역
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(
                        color = if (selectedOption == "phone") CustomColor.gray50 else Color.White,
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
                    .clickable { selectedOption = "phone" }
                    .padding(vertical = 16.dp, horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = com.markoala.tomoandroid.R.drawable.ic_call),
                    contentDescription = "call",
                    tint = CustomColor.black
                )
                Spacer(modifier = Modifier.width(4.dp))
                CustomText(
                    text = "전화번호",
                    type = CustomTextType.bodyMedium,
                    fontSize = 16.sp
                )
            }

            // 이메일 선택 영역
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(
                        color = if (selectedOption == "email") CustomColor.gray50 else Color.White,
                        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                    )
                    .clickable { selectedOption = "email" }
                    .padding(vertical = 16.dp, horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = com.markoala.tomoandroid.R.drawable.ic_email_at),
                    contentDescription = "@",
                    tint = CustomColor.black
                )
                Spacer(modifier = Modifier.width(4.dp))
                CustomText(
                    text = "유저이메일",
                    type = CustomTextType.bodyMedium,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 검색 필드 - email 선택 시에만 표시
        if (selectedOption == "email") {
            CustomText(
                text = "유저이메일",
                type = CustomTextType.bodyMedium,
                color = CustomColor.black
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = {
                        CustomText(
                            text = "이메일로 친구 찾기",
                            type = CustomTextType.bodyMedium,
                            color = CustomColor.gray300
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSearching
                )

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    modifier = Modifier
                        .height(56.dp)
                        .width(56.dp)
                        .border(
                            width = 1.dp,
                            color = if (isSearching) CustomColor.gray300 else CustomColor.black,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = !isSearching) {
                            searchFriend(searchText)
                        },
                    color = CustomColor.white,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = com.markoala.tomoandroid.R.drawable.ic_search),
                            contentDescription = "검색",
                            tint = if (isSearching) CustomColor.gray300 else CustomColor.black
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // 검색 결과 표시
            if (searchResult != null) {
                DashedBorderBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    borderColor = CustomColor.gray100,
                    borderWidth = 1.dp
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(16.dp),
                        color = CustomColor.gray30
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CustomText(
                                text = searchResult!!,
                                type = CustomTextType.bodyLarge,
                                fontSize = 14.sp,
                                color = CustomColor.black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 안내 메시지
        if (searchResult == null) {
            DashedBorderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                borderColor = CustomColor.gray50,
                borderWidth = 1.dp
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(16.dp),
                    color = CustomColor.gray30
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CustomText(
                            text = if (selectedOption == "phone") "준비중입니다." else if (isSearching) "검색 중..." else "친구의 이메일을 입력하여\n새로운 친구를 추가해보세요!",
                            type = CustomTextType.bodyLarge,
                            fontSize = 14.sp,
                            color = CustomColor.gray300,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

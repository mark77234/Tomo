package com.markoala.tomoandroid.ui.main.friends

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
import com.markoala.tomoandroid.data.model.FriendData
import com.markoala.tomoandroid.data.repository.friends.FriendsRepository
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.DashedBorderBox
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.utils.ErrorHandler

@Composable
fun AddFriendsScreen(
    paddingValues: PaddingValues,
    onBackClick: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FriendData>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val friendsRepository = remember { FriendsRepository() }
    val toastManager = LocalToastManager.current

    // 친구 검색
    fun searchFriends() {
        if (searchText.isBlank()) {
            toastManager.showWarning("이메일을 입력해주세요.")
            return
        }

        friendsRepository.getFriends(
            email = searchText,
            context = context,
            onLoading = { loading ->
                isSearching = loading
                if (loading) {
                    searchResults = emptyList()
                    errorMessage = null
                }
            },
            onSuccess = { friends ->
                searchResults = friends
                errorMessage = null
                if (friends.isEmpty()) {
                    toastManager.showInfo("검색 결과가 없습니다.")
                } else {
                    toastManager.showSuccess("사용자를 찾았습니다.")
                }
            },
            onError = { error ->
                searchResults = emptyList()
                errorMessage = error

                // ErrorHandler를 사용하여 에러 처리
                val errorResult = ErrorHandler.handleFriendSearchError(error)
                ErrorHandler.showToast(toastManager, errorResult)
            }
        )
    }

    // 친구 추가
    fun addFriend(email: String) {
        friendsRepository.postFriends(
            email = email,
            context = context,
            onLoading = { loading ->
                isSearching = loading
            },
            onSuccess = {
                // 친구 추가 성공 시 검색 결과 갱신
                toastManager.showSuccess("친구가 성공적으로 추가되었습니다!")
                searchFriends()
            },
            onError = { error ->
                errorMessage = error

                // ErrorHandler를 사용하여 에러 처리
                val errorResult = ErrorHandler.handleFriendAddError(error)
                ErrorHandler.showToast(toastManager, errorResult)
            }
        )
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
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSearching,
                    placeholder = {
                        CustomText(
                            text = "이메일을 입력하세요",
                            type = CustomTextType.bodyMedium,
                            color = CustomColor.gray300
                        )
                    }
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
                            searchFriends() // 친구 검색
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
            if (searchResults.isNotEmpty()) {
                for (friend in searchResults) {
                    DashedBorderBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
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
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    CustomText(
                                        text = friend.username,
                                        type = CustomTextType.bodyLarge,
                                        fontSize = 16.sp,
                                        color = CustomColor.black
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    CustomText(
                                        text = friend.email,
                                        type = CustomTextType.bodyMedium,
                                        fontSize = 14.sp,
                                        color = CustomColor.gray300
                                    )
                                }

                                Surface(
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = CustomColor.black,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            addFriend(friend.email)
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White
                                ) {
                                    Box(
                                        modifier = Modifier.padding(
                                            vertical = 8.dp,
                                            horizontal = 12.dp
                                        )
                                    ) {
                                        CustomText(
                                            text = "친구 추가",
                                            type = CustomTextType.titleSmall,
                                            fontSize = 12.sp,
                                            color = CustomColor.black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (errorMessage != null) {
                // 에러 메시지 표시
                DashedBorderBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    borderColor = CustomColor.error,
                    borderWidth = 1.dp
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(16.dp),
                        color = CustomColor.error.copy(alpha = 0.1f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CustomText(
                                text = errorMessage!!,
                                type = CustomTextType.bodyLarge,
                                fontSize = 14.sp,
                                color = CustomColor.error,
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
        if (searchResults.isEmpty() && errorMessage == null) {
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

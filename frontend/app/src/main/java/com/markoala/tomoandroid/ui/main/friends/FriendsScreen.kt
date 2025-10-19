package com.markoala.tomoandroid.ui.main.friends

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.DashedBorderBox
import com.markoala.tomoandroid.ui.main.friends.components.FriendCard
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun FriendsScreen(
    paddingValues: PaddingValues,
    onAddFriendsClick: () -> Unit = {},
    viewModel: FriendsViewModel = viewModel()
) {
    val friends by viewModel.friends.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // 화면 진입 시마다 친구 목록 새로고침
    LaunchedEffect(Unit) {
        viewModel.refreshFriends()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomText(
                text = "친구 목록",
                type = CustomTextType.headline,
                fontSize = 20.sp
            )
            Surface(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = CustomColor.gray100,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .clickable { onAddFriendsClick() },
                shape = RoundedCornerShape(32.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_addfriend
                        ),
                        contentDescription = "친구 추가",
                        tint = CustomColor.black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomText(
                        text = "친구추가",
                        type = CustomTextType.body,
                        fontSize = 14.sp,
                        color = CustomColor.black
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

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
                        text = "우정을 자랑하고 추억을 기록하세요!",
                        type = CustomTextType.body,
                        fontSize = 14.sp,
                        color = CustomColor.gray300
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = CustomColor.gray300,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                error != null -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CustomText(
                            text = "친구 목록을 불러올 수 없습니다",
                            type = CustomTextType.body,
                            fontSize = 16.sp,
                            color = CustomColor.gray300,
                            modifier = Modifier.padding(16.dp)
                        )
                        CustomText(
                            text = error ?: "알 수 없는 오류가 발생했습니다",
                            type = CustomTextType.body,
                            fontSize = 12.sp,
                            color = CustomColor.gray200,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = CustomColor.gray100,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.refreshFriends() },
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White
                        ) {
                            CustomText(
                                text = "다시 시도",
                                type = CustomTextType.body,
                                fontSize = 14.sp,
                                color = CustomColor.black,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                friends.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CustomText(
                            text = "친구가 없습니다",
                            type = CustomTextType.body,
                            fontSize = 14.sp,
                            color = CustomColor.gray300,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                else -> {
                    friends.forEach { friend ->
                        FriendCard(
                            friend = friend,
                            onFriendDeleted = { viewModel.refreshFriends() }
                        )
                    }
                }
            }
        }
    }
}

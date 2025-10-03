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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.DashedBorderBox
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun AddFriendsScreen(
    paddingValues: PaddingValues,
    onBackClick: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }

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
        // 검색 필드
        CustomText(
            text = "유저 이메일",
            type = CustomTextType.bodyMedium,
            color = CustomColor.black,
            fontSize = 14.sp
        )
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
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 안내 메시지
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
                        text = "친구의 이메일을 입력하여\n새로운 친구를 추가해보세요!",
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

package com.markoala.tomoandroid.ui.main

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.data.model.FriendProfile
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.DashedBorderBox
import com.markoala.tomoandroid.ui.components.friends.FriendCard
import com.markoala.tomoandroid.ui.theme.CustomColor
import java.time.LocalDate

private val sampleFriends = listOf(
    FriendProfile("김토모", "tomoKim@gmail.com", LocalDate.of(2024, 1, 15), 70),
    FriendProfile("이토모", "tomoLee@gmail.com", LocalDate.of(2023, 8, 22), 80),
    FriendProfile("박토모", "tomoPark@gmail.com", LocalDate.of(2025, 9, 10), 30),
    FriendProfile("정토모", "tomoJung@gmail.com", LocalDate.of(2022, 5, 3), 90),
    FriendProfile("최토모", "tomoChoi@gmail.com", LocalDate.of(2024, 11, 28), 60),
    FriendProfile("한토모", "tomoHan@gmail.com", LocalDate.of(2025, 7, 8), 50)
)


@Composable
fun FriendsScreen(paddingValues: PaddingValues) {
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
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            CustomText(
                text = "친구 목록",
                type = CustomTextType.headlineLarge,
                fontSize = 20.sp
            )
            Surface(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = CustomColor.gray100,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .clickable { /* TODO: 모임 생성 액션 */ },
                shape = RoundedCornerShape(32.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(
                            id = com.markoala.tomoandroid.R.drawable.ic_addfriend
                        ),
                        contentDescription = "친구 추가",
                        tint = CustomColor.black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomText(
                        text = "친구추가",
                        type = CustomTextType.bodyMedium,
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
            shape = RoundedCornerShape(8.dp),
            borderColor = CustomColor.gray50,
            borderWidth = 1.dp
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(8.dp),
                color = CustomColor.gray30
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CustomText(
                        text = "우정을 자랑하고 추억을 기록하세요!",
                        type = CustomTextType.bodyLarge,
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
            sampleFriends.forEach { friend ->
                FriendCard(friend)
            }
        }
    }
}

package com.markoala.tomoandroid.ui.main

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.data.model.Meeting
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.home.MeetingCard

import com.markoala.tomoandroid.ui.theme.CustomColor

private val sampleMeetings = listOf(
    Meeting("주말 브런치", "강남역 11번 출구", "토요일 11:00", 3),
    Meeting("영화 모임", "잠실 롯데시네마", "금요일 19:30", 5),
    Meeting("스터디", "온라인 Google Meet", "수요일 20:00", 4)
)

@Composable
fun HomeScreen(paddingValues: PaddingValues) {

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
                text = "모임 목록",
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
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "모임 추가",
                        tint = CustomColor.black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomText(
                        text = "모임 생성",
                        type = CustomTextType.bodyMedium,
                        fontSize = 14.sp,
                        color = CustomColor.black
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(sampleMeetings) { meeting ->
                MeetingCard(meeting)
            }
        }
    }
}

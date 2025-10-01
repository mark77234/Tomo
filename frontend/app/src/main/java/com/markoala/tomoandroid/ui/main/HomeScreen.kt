package com.markoala.tomoandroid.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.home.MeetingCard
import com.markoala.tomoandroid.ui.components.home.MeetingSummary

private val sampleMeetings = listOf(
    MeetingSummary("주말 브런치", "강남역 11번 출구", "토요일 11:00", 3),
    MeetingSummary("영화 모임", "잠실 롯데시네마", "금요일 19:30", 5),
    MeetingSummary("스터디", "온라인 Google Meet", "수요일 20:00", 4)
)

@Composable
fun HomeScreen(paddingValues: PaddingValues) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(sampleMeetings) { meeting ->
                MeetingCard(meeting)
            }
        }
    }
}

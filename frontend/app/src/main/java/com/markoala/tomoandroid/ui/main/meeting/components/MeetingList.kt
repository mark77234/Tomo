package com.markoala.tomoandroid.ui.main.meeting.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.data.model.MoimListDTO
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.theme.CustomColor
import kotlinx.coroutines.delay

@Composable
fun MeetingListContent(
    paddingValues: PaddingValues,
    userName: String,
    meetings: List<MoimListDTO>,
    onPlanMeetingClick: () -> Unit,
    onMeetingClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.white)
            .padding(paddingValues),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 상단 인사 카드
        item { GreetingCard(userName) }

        // 모임 생성 버튼
        item {
            CustomButton(
                text = "새 모임 만들기",
                onClick = onPlanMeetingClick,
                style = ButtonStyle.Primary
            )
        }

        // 섹션 타이틀
        item {
            MeetingListHeader()
        }

        // 리스트 상태 렌더링
        if (meetings.isEmpty()) {
            item {
                EmptyMeetingState()
            }
        } else {
            itemsIndexed(meetings) { index, meeting ->
                AnimatedMeetingCard(index) {
                    MeetingCard(
                        meeting = meeting,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onMeetingClick(meeting.moimId) }
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedMeetingCard(index: Int, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 40L) // stagger animation
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(250)) +
                slideInVertically(initialOffsetY = { it / 4 }, animationSpec = tween(250)),
        exit = fadeOut()
    ) {
        content()
    }
}

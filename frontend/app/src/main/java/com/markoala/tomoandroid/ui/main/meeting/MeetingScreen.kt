package com.markoala.tomoandroid.ui.main.meeting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.main.meeting.components.MeetingCard
import com.markoala.tomoandroid.ui.theme.CustomColor
import kotlinx.coroutines.delay

@Composable
fun MeetingScreen(
    paddingValues: PaddingValues,
    userName: String,
    onPlanMeetingClick: () -> Unit,
    onMeetingClick: (Int) -> Unit = {},
    meetingViewModel: MeetingViewModel = viewModel()
) {
    val meetings by meetingViewModel.meetings.collectAsState()
    val isLoading by meetingViewModel.isLoading.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                meetingViewModel.fetchMeetings()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val modifier = Modifier
        .fillMaxSize()
        .background(CustomColor.white)
        .padding(paddingValues)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { GreetingBlock(userName) }
        item {
            CustomButton(
                text = "새 모임 만들기",
                onClick = onPlanMeetingClick,
                style = ButtonStyle.Primary
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                CustomText(
                    text = "모임 타임라인",
                    type = CustomTextType.title,
                    color = CustomColor.textPrimary
                )
                CustomText(
                    text = "완료된 모임과 예정된 모임을 한눈에",
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )
            }
        }

        when {
            isLoading -> {
                item {
                    BoxedState {
                        CircularProgressIndicator(color = CustomColor.primary)
                    }
                }
            }

            meetings.isEmpty() -> {
                item {
                    BoxedState {
                        CustomText(
                            text = "생성된 모임이 없습니다.",
                            type = CustomTextType.body,
                            color = CustomColor.textSecondary
                        )
                    }
                }
            }

            else -> {
                itemsIndexed(meetings) { index, meeting ->
                    AnimatedMeetingCard(index = index) {
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
}

@Composable
private fun GreetingBlock(userName: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        CustomText(
            text = if (userName.isNotBlank()) "안녕하세요, $userName 님" else "오늘은 어떤 추억을 남길까요?",
            type = CustomTextType.display,
            color = CustomColor.textPrimary
        )
        CustomText(
            text = "따뜻한 우정을 기록해 보세요",
            type = CustomTextType.body,
            color = CustomColor.textSecondary
        )
    }
}

@Composable
private fun BoxedState(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(24.dp),
        color = CustomColor.surface
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@Composable
private fun AnimatedMeetingCard(index: Int, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 40L)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 250)) +
            slideInVertically(initialOffsetY = { it / 4 }, animationSpec = tween(durationMillis = 250)),
        exit = fadeOut()
    ) {
        content()
    }
}

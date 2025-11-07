package com.markoala.tomoandroid.ui.main.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.main.home.components.MeetingCard
import com.markoala.tomoandroid.ui.theme.CustomColor
import kotlinx.coroutines.delay

enum class HomeScreenMode { Overview, Meetings }

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    userName: String,
    mode: HomeScreenMode,
    onPlanMeetingClick: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val meetings = homeViewModel.meetings.collectAsState().value
    val isLoading = homeViewModel.isLoading.collectAsState().value
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                homeViewModel.fetchMeetings()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.background)
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GreetingBlock(userName = userName)
            }

            if (mode == HomeScreenMode.Overview) {
                item {
                    QuickActions(onPlanMeetingClick)
                }
            } else {
                item {
                    CustomButton(
                        text = "새 모임 만들기",
                        onClick = onPlanMeetingClick,
                        style = ButtonStyle.Primary
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CustomText(
                        text = if (mode == HomeScreenMode.Overview) "다가오는 모임" else "모임 타임라인",
                        type = CustomTextType.title,
                        color = CustomColor.textPrimary
                    )
                    CustomText(
                        text = if (mode == HomeScreenMode.Overview) "친구와의 약속을 놓치지 마세요" else "완료된 모임과 예정된 모임을 한눈에",
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
                            MeetingCard(meeting = meeting, modifier = Modifier.fillMaxWidth())
                        }
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
private fun QuickActions(onPlanMeetingClick: () -> Unit) {
    QuickActionCard(
        modifier = Modifier.fillMaxWidth(),
        title = "모임 생성",
        description = "친구들과 새로운 모임을 만들어보세요.",
        onClick = onPlanMeetingClick
    )
}

@Composable
private fun QuickActionCard(modifier: Modifier = Modifier, title: String, description: String, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = CustomColor.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = CircleShape,
                    color = CustomColor.primary.copy(alpha = 0.12f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = CustomColor.primary,
                        modifier = Modifier.padding(6.dp)
                    )
                }
                CustomText(text = title, type = CustomTextType.title, color = CustomColor.textPrimary)
            }
            CustomText(text = description, type = CustomTextType.bodySmall, color = CustomColor.textSecondary)
        }
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

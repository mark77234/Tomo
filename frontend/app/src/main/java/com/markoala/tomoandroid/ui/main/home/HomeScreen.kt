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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
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
    onAddFriendsClick: () -> Unit,
    onAffinityTabClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    val baseModifier = Modifier
        .fillMaxSize()
        .background(CustomColor.background)
        .padding(paddingValues)

    when (mode) {
        HomeScreenMode.Overview -> HomeOverviewContent(
            modifier = baseModifier,
            userName = userName,
            onPlanMeetingClick = onPlanMeetingClick,
            onAddFriendsClick = onAddFriendsClick,
            onAffinityTabClick = onAffinityTabClick,
            onSettingsClick = onSettingsClick,
            onProfileClick = onProfileClick
        )

        HomeScreenMode.Meetings -> MeetingTimelineContent(
            modifier = baseModifier,
            userName = userName,
            onPlanMeetingClick = onPlanMeetingClick
        )
    }
}

@Composable
private fun HomeOverviewContent(
    modifier: Modifier,
    userName: String,
    onPlanMeetingClick: () -> Unit,
    onAddFriendsClick: () -> Unit,
    onAffinityTabClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { GreetingBlock(userName) }
        item { OverviewHeroCard(userName, onPlanMeetingClick) }
        item {
            OverviewNavigationSection(
                onAddFriendsClick = onAddFriendsClick,
                onAffinityTabClick = onAffinityTabClick,
                onSettingsClick = onSettingsClick,
                onProfileClick = onProfileClick
            )
        }
    }
}

@Composable
private fun OverviewHeroCard(userName: String, onPlanMeetingClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = CustomColor.primaryContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomText(
                text = if (userName.isNotBlank()) "${userName}님, 새로운 모임을 열어볼까요?" else "오늘 어떤 만남을 기록할까요?",
                type = CustomTextType.title,
                color = CustomColor.primary
            )
            CustomText(
                text = "모임을 만들고 친구들에게 초대장을 보내보세요.",
                type = CustomTextType.bodySmall,
                color = CustomColor.primaryDim
            )
            CustomButton(
                text = "모임 생성",
                onClick = onPlanMeetingClick,
                modifier = Modifier.fillMaxWidth(),
                style = ButtonStyle.Primary
            )
        }
    }
}

@Composable
private fun OverviewNavigationSection(
    onAddFriendsClick: () -> Unit,
    onAffinityTabClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    val actions = listOf(
        OverviewAction(
            title = "친구 추가",
            description = "코드 공유나 검색으로 친구를 초대해요",
            icon = painterResource(id =com.markoala.tomoandroid.R.drawable.ic_addfriend),
            onClick = onAddFriendsClick
        ),
        OverviewAction(
            title = "친밀도 탭",
            description = "친구들과의 스토리와 레벨을 확인해요",
            icon = Icons.Filled.Favorite,
            onClick = onAffinityTabClick
        ),
        OverviewAction(
            title = "설정",
            description = "계정과 알림, 보안을 관리해요",
            icon = Icons.Filled.Settings,
            onClick = onSettingsClick
        ),
        OverviewAction(
            title = "내 정보",
            description = "프로필과 기본 정보를 확인해요",
            icon = Icons.Filled.Person,
            onClick = onProfileClick
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CustomText(
            text = "토모 허브",
            type = CustomTextType.title,
            color = CustomColor.textPrimary
        )
        CustomText(
            text = "필요한 탭으로 바로 이동해 보세요",
            type = CustomTextType.bodySmall,
            color = CustomColor.textSecondary
        )
        actions.chunked(2).forEach { rowActions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowActions.forEach { action ->
                    OverviewActionCard(
                        action = action,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowActions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun OverviewActionCard(action: OverviewAction, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .heightIn(min = 140.dp)
            .clickable { action.onClick() },
        shape = RoundedCornerShape(24.dp),
        color = CustomColor.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = CustomColor.primary.copy(alpha = 0.12f)
            ) {
                // ImageVector or Painter 둘 다 처리
                when (val ic = action.icon) {
                    is ImageVector -> Icon(
                        imageVector = ic,
                        contentDescription = action.title,
                        tint = CustomColor.primary,
                        modifier = Modifier.padding(10.dp)
                    )

                    is Painter -> Icon(
                        painter = ic,
                        contentDescription = action.title,
                        tint = CustomColor.primary,
                        modifier = Modifier.padding(10.dp)
                    )

                    else -> {
                        // Fallback: 빈 공간
                        Spacer(modifier = Modifier.height(0.dp))
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CustomText(
                    text = action.title,
                    type = CustomTextType.title,
                    color = CustomColor.textPrimary
                )
                CustomText(
                    text = action.description,
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )
            }
        }
    }
}

private data class OverviewAction(
    val title: String,
    val description: String,
    val icon: Any,
    val onClick: () -> Unit,
)

@Composable
private fun MeetingTimelineContent(
    modifier: Modifier,
    userName: String,
    onPlanMeetingClick: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val meetings by homeViewModel.meetings.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                homeViewModel.fetchMeetings()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

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
                        MeetingCard(meeting = meeting, modifier = Modifier.fillMaxWidth())
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

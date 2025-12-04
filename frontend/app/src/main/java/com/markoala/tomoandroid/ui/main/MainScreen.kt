package com.markoala.tomoandroid.ui.main

import androidx.annotation.DrawableRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.activity.compose.BackHandler
import androidx.navigation.NavHostController
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.ui.main.components.ChromeScaffold
import com.markoala.tomoandroid.ui.main.components.MainScreenRenderer

enum class BottomTab(val label: String, @param:DrawableRes val iconRes: Int) {
    Home("홈", R.drawable.ic_home),
    Meetings("모임", R.drawable.ic_friends),
    Calendar("달력", R.drawable.ic_calendar),
    Affinity("친구", R.drawable.ic_profile),
    Map("지도", R.drawable.ic_location)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onSignOut: () -> Unit,
    deepLinkInviteCode: String? = null,
    onInviteCodeConsumed: () -> Unit = {}
) {
    var pendingInviteCode by remember { mutableStateOf(deepLinkInviteCode) }

    val navigator = remember { MainNavigator() }
    val current = navigator.currentEntry
    val currentTab = navigator.currentTab
    val stack = navigator.stack
    val viewModel = remember { MainViewModel() }


    BackHandler(enabled = stack.size > 1) {
        navigator.pop()
    }

    // 딥링크로 초대코드를 받았을 때 친구 추가 화면으로 이동
    LaunchedEffect(deepLinkInviteCode) {
        if (deepLinkInviteCode != null) {
            pendingInviteCode = deepLinkInviteCode
            navigator.push(MainStackEntry.AddFriends(deepLinkInviteCode))
            onInviteCodeConsumed() // 딥링크 코드 사용 후 초기화
        }
    }

    val showChrome = current is MainStackEntry.Tab

    ChromeScaffold(
        showChrome = showChrome,
        currentTab = currentTab,
        name = viewModel.name,
        onProfileClick = { navigator.push(MainStackEntry.Profile) },
        onTabSelected = { navigator.openTab(it) }
    ) { padding ->

        MainScreenRenderer(
            entry = current,
            padding = padding,
            userInfo = viewModel,
            navigator = navigator,
            pendingInviteCode = pendingInviteCode,
            onInviteCodeConsumed = {
                pendingInviteCode = null
                onInviteCodeConsumed()
            },
            onSignOut = onSignOut
        )
    }
}

sealed interface MainStackEntry {
    data class Tab(val tab: BottomTab) : MainStackEntry
    data class MeetingDetail(val moimId: Int) : MainStackEntry
    data class CalendarDetail(val eventId: Int) : MainStackEntry
    data class AddFriends(val inviteCode: String? = null) : MainStackEntry
    object CreateMeeting : MainStackEntry
    object Profile : MainStackEntry
}

package com.markoala.tomoandroid.ui.main

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.ui.components.BottomNavigationBar
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.main.components.MainHeader
import com.markoala.tomoandroid.ui.main.friends.AddFriendsScreen
import com.markoala.tomoandroid.ui.main.friends.FriendsScreen
import com.markoala.tomoandroid.ui.main.home.HomeScreen
import com.markoala.tomoandroid.ui.main.meeting.create_meeting.CreateMeetingScreen
import com.markoala.tomoandroid.ui.main.meeting.MeetingScreen
import com.markoala.tomoandroid.ui.main.meeting.meeting_detail.MeetingDetailScreen
import com.markoala.tomoandroid.ui.main.profile.ProfileScreen
import com.markoala.tomoandroid.ui.main.settings.SettingsScreen
import com.markoala.tomoandroid.ui.theme.CustomColor

enum class BottomTab(val label: String, @param:DrawableRes val iconRes: Int) {
    Home("홈", R.drawable.ic_home),
    Meetings("모임", R.drawable.ic_friends),
    Affinity("친구", R.drawable.ic_profile),
    Settings("설정", R.drawable.ic_setting)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: androidx.navigation.NavHostController,
    onSignOut: () -> Unit,
    deepLinkInviteCode: String? = null,
    onInviteCodeConsumed: () -> Unit = {}
) {
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }
    val user = firebaseAuth.currentUser
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var pendingInviteCode by remember { mutableStateOf(deepLinkInviteCode) }

    // 스택 기반 라우팅 상태
    val backStack = remember {
        mutableStateListOf<MainStackEntry>(MainStackEntry.Tab(BottomTab.Home))
    }
    val currentEntry by remember { derivedStateOf { backStack.last() } }
    val currentTab by remember { derivedStateOf { backStack.lastOrNull { it is MainStackEntry.Tab } as? MainStackEntry.Tab } }

    LaunchedEffect(user) {
        user?.let {
            firestore.collection("users").document(it.uid).get()
                .addOnSuccessListener { doc ->
                    name = doc.getString("name") ?: ""
                    email = doc.getString("email") ?: ""
                    userId = doc.getString("uid") ?: ""
                }
        }
    }

    fun push(entry: MainStackEntry) {
        if (backStack.lastOrNull() == entry && entry !is MainStackEntry.Tab) return
        backStack.add(entry)
    }

    fun pop() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    fun openTab(tab: BottomTab) {
        val tabEntry = MainStackEntry.Tab(tab)
        if (backStack.lastOrNull() == tabEntry) return
        backStack.add(tabEntry)
    }

    fun openAddFriends(inviteCode: String? = null) {
        if (currentTab?.tab != BottomTab.Affinity) {
            backStack.add(MainStackEntry.Tab(BottomTab.Affinity))
        }
        backStack.add(MainStackEntry.AddFriends(inviteCode))
    }

    BackHandler(enabled = backStack.size > 1) {
        pop()
    }

    // 딥링크로 초대코드를 받았을 때 친구 추가 화면으로 이동
    LaunchedEffect(deepLinkInviteCode) {
        if (deepLinkInviteCode != null) {
            pendingInviteCode = deepLinkInviteCode
            openAddFriends(deepLinkInviteCode)
            onInviteCodeConsumed() // 딥링크 코드 사용 후 초기화
        }
    }

    val showChrome = currentEntry is MainStackEntry.Tab

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CustomColor.background,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        topBar = {
            if (showChrome) {
                MainHeader(
                    subtitle = if (name.isNotBlank()) "${name}님, 토모와 함께해요" else "친구와의 순간을 기록해요",
                    onProfileClick = { push(MainStackEntry.Profile) }
                )
            }
        },
        bottomBar = {
            if (showChrome) {
                BottomNavigationBar(selectedTab = currentTab?.tab ?: BottomTab.Home, onTabSelected = { openTab(it) })
            }
        }
    ) { paddingValues ->
        val screenPadding = paddingValues
        when (val entry = currentEntry) {
            is MainStackEntry.Tab -> {
                Crossfade(
                    targetState = entry.tab,
                    animationSpec = tween(durationMillis = 150),
                    label = "main-tabs"
                ) { tab ->
                    when (tab) {
                        BottomTab.Home -> HomeScreen(
                            paddingValues = screenPadding,
                            userName = name,
                            onPlanMeetingClick = { push(MainStackEntry.CreateMeeting) },
                            onAddFriendsClick = { openAddFriends() },
                            onAffinityTabClick = { openTab(BottomTab.Affinity) },
                            onMeetingClick = { openTab(BottomTab.Meetings) },
                            onProfileClick = { push(MainStackEntry.Profile) }
                        )

                        BottomTab.Meetings -> MeetingScreen(
                            paddingValues = screenPadding,
                            userName = name,
                            onPlanMeetingClick = { push(MainStackEntry.CreateMeeting) },
                            onMeetingClick = { moimId -> push(MainStackEntry.MeetingDetail(moimId)) }
                        )

                        BottomTab.Affinity -> FriendsScreen(
                            paddingValues = screenPadding,
                            onAddFriendsClick = { openAddFriends() }
                        )

                        BottomTab.Settings -> SettingsScreen(
                            paddingValues = screenPadding,
                            onSignOut = onSignOut,
                            onDeleteAccount = onSignOut
                        )
                    }
                }
            }

            is MainStackEntry.AddFriends -> AddFriendsScreen(
                paddingValues = screenPadding,
                userId = userId,
                inviteCode = entry.inviteCode ?: pendingInviteCode,
                onBackClick = { pop() },
                onInviteCodeConsumed = {
                    pendingInviteCode = null
                    onInviteCodeConsumed()
                }
            )

            is MainStackEntry.CreateMeeting -> CreateMeetingScreen(
                paddingValues = screenPadding,
                onBackClick = { pop() },
                onSuccess = {
                    pop()
                    openTab(BottomTab.Meetings)
                }
            )

            is MainStackEntry.MeetingDetail -> MeetingDetailScreen(
                moimId = entry.moimId,
                onBackClick = { pop() }
            )

            is MainStackEntry.Profile -> ProfileScreen(
                name = name,
                email = email,
                userId = userId,
                paddingValues = screenPadding,
                onClose = { pop() }
            )
        }
    }
}

private sealed interface MainStackEntry {
    data class Tab(val tab: BottomTab) : MainStackEntry
    data class MeetingDetail(val moimId: Int) : MainStackEntry
    data class AddFriends(val inviteCode: String? = null) : MainStackEntry
    object CreateMeeting : MainStackEntry
    object Profile : MainStackEntry
}


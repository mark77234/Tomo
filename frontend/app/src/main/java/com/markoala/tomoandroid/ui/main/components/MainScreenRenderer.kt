package com.markoala.tomoandroid.ui.main.components


import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.PaddingValues
import com.markoala.tomoandroid.ui.main.BottomTab
import com.markoala.tomoandroid.ui.main.MainNavigator
import com.markoala.tomoandroid.ui.main.MainStackEntry
import com.markoala.tomoandroid.ui.main.MainViewModel
import com.markoala.tomoandroid.ui.main.friends.AddFriendsScreen
import com.markoala.tomoandroid.ui.main.home.HomeScreen
import com.markoala.tomoandroid.ui.main.friends.FriendsScreen
import com.markoala.tomoandroid.ui.main.meeting.create_meeting.CreateMeetingScreen
import com.markoala.tomoandroid.ui.main.meeting.MeetingScreen
import com.markoala.tomoandroid.ui.main.meeting.meeting_detail.MeetingDetailScreen
import com.markoala.tomoandroid.ui.main.profile.ProfileScreen
import com.markoala.tomoandroid.ui.main.settings.SettingsScreen

@Composable
fun MainScreenRenderer(
    entry: MainStackEntry,
    padding: PaddingValues,
    userInfo: MainViewModel,
    navigator: MainNavigator,
    pendingInviteCode: String?,
    onInviteCodeConsumed: () -> Unit,
    onSignOut: () -> Unit
) {
    when (entry) {

        is MainStackEntry.Tab -> when (entry.tab) {

            BottomTab.Home -> HomeScreen(
                paddingValues = padding,
                userName = userInfo.name,
                onPlanMeetingClick = { navigator.push(MainStackEntry.CreateMeeting) },
                onAddFriendsClick = { navigator.push(MainStackEntry.AddFriends()) },
                onAffinityTabClick = { navigator.openTab(BottomTab.Affinity) },
                onMeetingClick = { navigator.openTab(BottomTab.Meetings) },
                onProfileClick = { navigator.push(MainStackEntry.Profile) }
            )

            BottomTab.Meetings -> MeetingScreen(
                paddingValues = padding,
                userName = userInfo.name,
                onPlanMeetingClick = { navigator.push(MainStackEntry.CreateMeeting) },
                onMeetingClick = { moimId -> navigator.push(MainStackEntry.MeetingDetail(moimId)) }
            )

            BottomTab.Affinity -> FriendsScreen(
                paddingValues = padding,
                onAddFriendsClick = { navigator.push(MainStackEntry.AddFriends()) }
            )

            BottomTab.Settings -> SettingsScreen(
                paddingValues = padding,
                onSignOut = onSignOut,
                onDeleteAccount = onSignOut
            )
        }

        is MainStackEntry.AddFriends -> AddFriendsScreen(
            paddingValues = padding,
            userId = userInfo.userId,
            inviteCode = entry.inviteCode ?: pendingInviteCode,
            onBackClick = { navigator.pop() },
            onInviteCodeConsumed = {
                onInviteCodeConsumed()
            }
        )

        is MainStackEntry.CreateMeeting -> CreateMeetingScreen(
            paddingValues = padding,
            onBackClick = { navigator.pop() },
            onSuccess = {
                navigator.pop()
                navigator.openTab(BottomTab.Meetings)
            }
        )

        is MainStackEntry.MeetingDetail -> MeetingDetailScreen(
            moimId = entry.moimId,
            onBackClick = { navigator.pop() }
        )

        is MainStackEntry.Profile -> ProfileScreen(
            name = userInfo.name,
            email = userInfo.email,
            userId = userInfo.userId,
            onClose = { navigator.pop() }
        )
    }
}

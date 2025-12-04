package com.markoala.tomoandroid.ui.main.components


import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.PaddingValues
import com.markoala.tomoandroid.ui.main.BottomTab
import com.markoala.tomoandroid.ui.main.MainNavigator
import com.markoala.tomoandroid.ui.main.MainStackEntry
import com.markoala.tomoandroid.ui.main.MainViewModel
import com.markoala.tomoandroid.ui.main.calendar.CalendarScreen
import com.markoala.tomoandroid.ui.main.calendar.calendar_detail.CalendarDetailScreen
import com.markoala.tomoandroid.ui.main.friends.AddFriendsScreen
import com.markoala.tomoandroid.ui.main.home.HomeScreen
import com.markoala.tomoandroid.ui.main.friends.FriendsScreen
import com.markoala.tomoandroid.ui.main.meeting.create_meeting.CreateMeetingScreen
import com.markoala.tomoandroid.ui.main.meeting.MeetingScreen
import com.markoala.tomoandroid.ui.main.meeting.meeting_detail.MeetingDetailScreen
import com.markoala.tomoandroid.ui.main.profile.ProfileScreen
import com.markoala.tomoandroid.ui.main.map.MapScreen

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
                onCalendarClick = { navigator.openTab(BottomTab.Calendar) }
            )

            BottomTab.Meetings -> MeetingScreen(
                paddingValues = padding,
                userName = userInfo.name,
                onPlanMeetingClick = { navigator.push(MainStackEntry.CreateMeeting) },
                onMeetingClick = { moimId -> navigator.push(MainStackEntry.MeetingDetail(moimId)) }
            )
            BottomTab.Calendar -> CalendarScreen(
                paddingValues = padding,
                onEventClick = { moimId ->
                    navigator.push(MainStackEntry.MeetingDetail(moimId))
                }
            )



            BottomTab.Affinity -> FriendsScreen(
                paddingValues = padding,
                onAddFriendsClick = { navigator.push(MainStackEntry.AddFriends()) }
            )

            BottomTab.Map -> MapScreen(
                paddingValues = padding
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
            onSignOut = onSignOut,
            onClose = { navigator.pop() }
        )

        is MainStackEntry.CalendarDetail -> CalendarDetailScreen(
            eventId = entry.eventId,
            onBackClick = { navigator.pop() }
        )


    }
}

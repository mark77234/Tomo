package com.markoala.tomoandroid.ui.main

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.ui.components.BottomNavigationBar
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.main.friends.AddFriendsScreen
import com.markoala.tomoandroid.ui.main.friends.FriendsScreen
import com.markoala.tomoandroid.ui.main.home.HomeScreen
import com.markoala.tomoandroid.ui.main.meeting.create_meeting.CreateMeetingScreen
import com.markoala.tomoandroid.ui.main.meeting.MeetingScreen
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
    onSignOut: () -> Unit
) {
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }
    val user = firebaseAuth.currentUser
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(BottomTab.Home) }
    var routingAddFriends by remember { mutableStateOf(false) }
    var routingCreateMeeting by remember { mutableStateOf(false) }
    var showProfile by remember { mutableStateOf(false) }

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

    val showChrome = !routingAddFriends && !routingCreateMeeting && !showProfile

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CustomColor.background,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        topBar = {
            if (showChrome) {
                MainTopBar(
                    subtitle = if (name.isNotBlank()) "${name}님, 토모와 함께해요" else "친구와의 순간을 기록해요",
                    onProfileClick = { showProfile = true }
                )
            }
        },
        bottomBar = {
            if (showChrome) {
                BottomNavigationBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
            }
        }
    ) { paddingValues ->
        val screenPadding = PaddingValues()
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(CustomColor.background)
        ) {
            when {
                routingCreateMeeting -> {
                    CreateMeetingScreen(
                        paddingValues = screenPadding,
                        onBackClick = { routingCreateMeeting = false },
                        onSuccess = {
                            routingCreateMeeting = false
                            selectedTab = BottomTab.Meetings
                        }
                    )
                }

                routingAddFriends -> {
                    AddFriendsScreen(
                        paddingValues = screenPadding,
                        userId = userId,
                        onBackClick = { routingAddFriends = false }
                    )
                }

                else -> {
                    Crossfade(
                        targetState = selectedTab,
                        animationSpec = tween(durationMillis = 150),
                        label = "main-tabs"
                    ) { tab ->
                        when (tab) {
                            BottomTab.Home -> HomeScreen(
                                paddingValues = screenPadding,
                                userName = name,
                                onPlanMeetingClick = { routingCreateMeeting = true },
                                onAddFriendsClick = { routingAddFriends = true },
                                onAffinityTabClick = { selectedTab = BottomTab.Affinity },
                                onMeetingClick = { selectedTab = BottomTab.Meetings },
                                onProfileClick = { showProfile = true }
                            )

                            BottomTab.Meetings -> MeetingScreen(
                                paddingValues = screenPadding,
                                userName = name,
                                onPlanMeetingClick = { routingCreateMeeting = true },
                                onMeetingClick = { moimTitle ->
                                    navController.navigate(
                                        com.markoala.tomoandroid.navigation.Screen.MeetingDetail.createRoute(moimTitle)
                                    )
                                }
                            )

                            BottomTab.Affinity -> FriendsScreen(
                                paddingValues = screenPadding,
                                onAddFriendsClick = { routingAddFriends = true }
                            )

                            BottomTab.Settings -> SettingsScreen(
                                paddingValues = screenPadding,
                                onSignOut = onSignOut,
                                onDeleteAccount = onSignOut
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = showProfile,
                        enter = fadeIn(animationSpec = tween(150)),
                        exit = fadeOut(animationSpec = tween(150))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f))
                        ) {
                            ProfileScreen(
                                name = name,
                                email = email,
                                userId = userId,
                                paddingValues = screenPadding,
                                onClose = { showProfile = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainTopBar(
    subtitle: String,
    onProfileClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "profile-press"
    )

    Surface(color = CustomColor.background) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.logo_tomo),
                    contentDescription = "Tomo Logo",
                    Modifier.width(60.dp)
                )
                CustomText(
                    text = subtitle,
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }

            Surface(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onProfileClick() },
                shape = CircleShape,
                color = CustomColor.white,
                shadowElevation = 6.dp
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "프로필 열기",
                    tint = CustomColor.primary,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(24.dp)
                )
            }
        }
    }
}

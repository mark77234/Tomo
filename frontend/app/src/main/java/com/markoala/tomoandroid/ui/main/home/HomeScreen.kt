package com.markoala.tomoandroid.ui.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.main.home.components.GreetingCard
import com.markoala.tomoandroid.ui.main.home.components.HeroCard
import com.markoala.tomoandroid.ui.main.home.components.NavigationSection
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    userName: String,
    onPlanMeetingClick: () -> Unit,
    onAddFriendsClick: () -> Unit,
    onAffinityTabClick: () -> Unit,
    onMeetingClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    val baseModifier = Modifier
        .fillMaxSize()
        .background(CustomColor.white)
        .padding(paddingValues)

    HomeOverviewContent(
        modifier = baseModifier,
        userName = userName,
        onPlanMeetingClick = onPlanMeetingClick,
        onAddFriendsClick = onAddFriendsClick,
        onAffinityTabClick = onAffinityTabClick,
        onMeetingClick = onMeetingClick,
        onProfileClick = onProfileClick
    )
}

@Composable
private fun HomeOverviewContent(
    modifier: Modifier,
    userName: String,
    onPlanMeetingClick: () -> Unit,
    onAddFriendsClick: () -> Unit,
    onAffinityTabClick: () -> Unit,
    onMeetingClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { GreetingCard(userName) }
        item { HeroCard(userName, onPlanMeetingClick) }
        item {
            NavigationSection(
                onAddFriendsClick = onAddFriendsClick,
                onAffinityTabClick = onAffinityTabClick,
                onMeetingClick = onMeetingClick,
                onProfileClick = onProfileClick
            )
        }
    }
}





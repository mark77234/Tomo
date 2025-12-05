package com.markoala.tomoandroid.ui.main.friends

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.data.model.FriendProfile
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LoadingDialog
import com.markoala.tomoandroid.ui.main.friends.components.FriendCard
import com.markoala.tomoandroid.ui.theme.CustomColor
import kotlinx.coroutines.delay

@Composable
fun FriendsScreen(
    paddingValues: PaddingValues,
    onAddFriendsClick: () -> Unit = {},
    viewModel: FriendsViewModel = viewModel()
) {
    val friends by viewModel.friends.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshFriends()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.white)
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Header(onAddFriendsClick)
        Spacer16()

        when {
            isLoading -> {
                LoadingDialog()
            }

            error != null -> {
                EmptyState(message = error ?: "친구 목록을 불러올 수 없습니다", actionText = "다시 시도") {
                    viewModel.refreshFriends()
                }
            }

            friends.isEmpty() -> {
                EmptyState(message = "아직 친구가 없습니다", actionText = "친구 추가") { onAddFriendsClick() }
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    itemsIndexed(
                        items = friends,
                        key = { _, friend -> friend.email }   // 고유값을 key로 지정
                    ) { index, friend ->
                        AnimatedFriendCard(index, friend) {
                            FriendCard(
                                friend = friend,
                                onFriendDeleted = { viewModel.refreshFriends() }
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun Header(onAddFriendsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            CustomText(text = "친구", type = CustomTextType.headline, color = CustomColor.textPrimary)
            CustomText(text = "토모와 함께하는 친구들", type = CustomTextType.bodySmall, color = CustomColor.textSecondary)
        }
        CustomButton(
            text = "친구 추가",
            onClick = onAddFriendsClick,
            style = ButtonStyle.Secondary
        )
    }
}

@Composable
private fun EmptyState(message: String, actionText: String, onAction: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        shape = RoundedCornerShape(24.dp),
        color = CustomColor.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomText(text = message, type = CustomTextType.body, color = CustomColor.textSecondary)
            CustomButton(text = actionText, onClick = onAction)
        }
    }
}

@Composable
private fun AnimatedFriendCard(
    index: Int,
    friend: FriendProfile,
    content: @Composable () -> Unit
) {
    var visible by rememberSaveable(friend.email) { mutableStateOf(false) }


    LaunchedEffect(friend.email) {
        delay(index * 40L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(250)) + slideInVertically(initialOffsetY = { it / 4 }, animationSpec = tween(250))
    ) {
        content()
    }
}

@Composable
private fun Spacer16() {
    Spacer(modifier = Modifier.height(16.dp))
}

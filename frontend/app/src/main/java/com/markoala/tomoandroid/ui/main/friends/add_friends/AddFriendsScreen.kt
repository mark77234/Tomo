package com.markoala.tomoandroid.ui.main.friends

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.data.model.friends.FriendSummary
import com.markoala.tomoandroid.data.repository.friends.FriendsRepository
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextField
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.theme.CustomColor

private enum class AddFriendsTab { Search, Share }

@Composable
fun AddFriendsScreen(
    paddingValues: PaddingValues,
    userId: String,
    onBackClick: () -> Unit
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FriendSummary>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(AddFriendsTab.Search) }
    val friendsRepository = remember { FriendsRepository() }
    val toastManager = LocalToastManager.current
    val clipboardManager = LocalClipboardManager.current

    fun searchFriends() {
        if (searchText.isBlank()) {
            toastManager.showWarning("이메일을 입력해주세요.")
            return
        }

        friendsRepository.getFriends(
            email = searchText,
            onLoading = { loading ->
                isSearching = loading
                if (loading) {
                    searchResults = emptyList()
                    errorMessage = null
                }
            },
            onSuccess = { friends ->
                searchResults = friends
                errorMessage = null
                if (friends.isEmpty()) {
                    toastManager.showInfo("검색 결과가 없습니다.")
                } else {
                    toastManager.showSuccess("사용자를 찾았습니다.")
                }
            },
            onError = { error ->
                searchResults = emptyList()
                errorMessage = error
                toastManager.showWarning(error)
            }
        )
    }

    fun addFriend(email: String) {
        friendsRepository.postFriends(
            email = email,
            onLoading = { loading -> isSearching = loading },
            onSuccess = {
                toastManager.showSuccess("친구가 성공적으로 추가되었습니다!")
                searchResults = emptyList()
                searchText = ""
            },
            onError = { error ->
                errorMessage = error
                toastManager.showWarning(error)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.background)
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Header(onBackClick)
        Spacer(modifier = Modifier.height(24.dp))
        TabSelector(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        Spacer(modifier = Modifier.height(24.dp))

        when (selectedTab) {
            AddFriendsTab.Search -> {
                SearchFriendsSection(
                    searchText = searchText,
                    onSearchTextChange = { searchText = it },
                    isSearching = isSearching,
                    searchResults = searchResults,
                    errorMessage = errorMessage,
                    onSearch = { searchFriends() },
                    onAddFriend = { addFriend(it) }
                )
            }

            AddFriendsTab.Share -> {
                ShareInviteSection(userId = userId, onCopy = {
                    val invite = generateInviteCode(userId)
                    clipboardManager.setText(AnnotatedString(invite))
                    toastManager.showSuccess("초대 코드가 복사되었습니다.")
                })
            }
        }
    }
}

@Composable
private fun Header(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            CustomText(text = "친구 추가", type = CustomTextType.headline, color = CustomColor.textPrimary)
            CustomText(text = "검색하거나 초대 코드를 공유하세요", type = CustomTextType.bodySmall, color = CustomColor.textSecondary)
        }
        CustomButton(text = "목록 보기", onClick = onBackClick, style = ButtonStyle.Secondary)
    }
}

@Composable
private fun TabSelector(selectedTab: AddFriendsTab, onTabSelected: (AddFriendsTab) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AddFriendsTab.values().forEach { tab ->
            val isSelected = tab == selectedTab
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clickable { onTabSelected(tab) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) CustomColor.primary else CustomColor.surface
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CustomText(
                        text = if (tab == AddFriendsTab.Search) "검색" else "공유",
                        type = CustomTextType.body,
                        color = if (isSelected) CustomColor.white else CustomColor.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchFriendsSection(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    isSearching: Boolean,
    searchResults: List<FriendSummary>,
    errorMessage: String?,
    onSearch: () -> Unit,
    onAddFriend: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CustomTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            placeholder = "이메일을 입력하세요",
            enabled = !isSearching
        )
        CustomButton(
            text = if (isSearching) "검색 중..." else "친구 검색",
            onClick = onSearch,
            enabled = !isSearching
        )

        when {
            errorMessage != null -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = CustomColor.danger.copy(alpha = 0.1f)
                ) {
                    CustomText(
                        text = errorMessage,
                        type = CustomTextType.bodySmall,
                        color = CustomColor.danger,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            searchResults.isEmpty() -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = CustomColor.surface
                ) {
                    CustomText(
                        text = if (isSearching) "검색 중입니다..." else "친구의 이메일을 입력하여 새로운 친구를 추가해보세요!",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.textSecondary,
                        modifier = Modifier.padding(20.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(searchResults) { friend ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = CustomColor.surface,
                            border = BorderStroke(1.dp, CustomColor.outline)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                                    CustomText(text = friend.username, type = CustomTextType.body, color = CustomColor.textPrimary)
                                    CustomText(text = friend.email, type = CustomTextType.bodySmall, color = CustomColor.textSecondary)
                                }
                                CustomButton(text = "추가", onClick = { onAddFriend(friend.email) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShareInviteSection(userId: String, onCopy: () -> Unit) {
    val inviteCode = generateInviteCode(userId)
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = CustomColor.surface,
            border = BorderStroke(1.dp, CustomColor.outline)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomText(text = "초대 코드", type = CustomTextType.title, color = CustomColor.textPrimary)
                CustomText(text = inviteCode, type = CustomTextType.display, color = CustomColor.textPrimary)
                CustomText(
                    text = "친구에게 코드를 공유하면 간편하게 추가할 수 있어요.",
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )
            }
        }
        CustomButton(text = "초대 코드 복사", onClick = onCopy, style = ButtonStyle.Primary)
    }
}

private fun generateInviteCode(userId: String): String {
    return if (userId.isNotBlank() && userId.length >= 4) {
        "TOMO-${userId.takeLast(4).uppercase()}"
    } else {
        "TOMO-0000"
    }
}

package com.markoala.tomoandroid.ui.main.friends

import android.content.ClipData
import android.content.Context
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.platform.LocalContext
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
import com.markoala.tomoandroid.ui.main.friends.add_friends.components.AddFriendsHeader
import com.markoala.tomoandroid.ui.main.friends.add_friends.components.SearchFriendsSection
import com.markoala.tomoandroid.ui.main.friends.add_friends.components.ShareInviteSection
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.util.generateInviteCode

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
    val context = LocalContext.current

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
        AddFriendsHeader(onBackClick)
        Spacer(modifier = Modifier.height(24.dp))

        // 중앙 정렬 컨테이너
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Column(modifier = Modifier.widthIn(max = 600.dp)) {
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
                            // Android clipboard 사용
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = ClipData.newPlainText("invite", invite)
                            clipboard.setPrimaryClip(clip)
                            toastManager.showSuccess("초대 코드가 복사되었습니다.")
                        })
                    }
                }
            }
        }
    }
}



@Composable
private fun TabSelector(selectedTab: AddFriendsTab, onTabSelected: (AddFriendsTab) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AddFriendsTab.entries.forEach { tab ->
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



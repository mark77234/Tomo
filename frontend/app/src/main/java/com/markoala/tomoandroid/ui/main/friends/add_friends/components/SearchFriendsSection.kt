package com.markoala.tomoandroid.ui.main.friends.add_friends.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.data.model.friends.FriendSummary
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextField
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun SearchFriendsSection(
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

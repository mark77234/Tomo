package com.markoala.tomoandroid.ui.main.friends.add_friends.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.data.model.friends.FriendSummary
import com.markoala.tomoandroid.ui.components.ButtonStyle
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
        // 검색 입력 카드
        Surface(
            modifier = Modifier.fillMaxWidth()
                .border(1.dp,CustomColor.gray300, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.white
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(CustomColor.primary)
                    )
                    CustomText(
                        text = "친구 검색",
                        type = CustomTextType.body,
                        color = CustomColor.primary
                    )
                }

                CustomTextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    placeholder = "이메일 또는 친구코드를 입력하세요",
                    enabled = !isSearching
                )

                CustomButton(
                    text = if (isSearching) "검색 중..." else "친구 검색",
                    onClick = onSearch,
                    enabled = !isSearching,
                    modifier = Modifier.fillMaxWidth(),
                    style = ButtonStyle.Primary
                )
            }
        }

        when {
            errorMessage != null -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = CustomColor.danger.copy(alpha = 0.05f)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notification),
                            contentDescription = null,
                            tint = CustomColor.danger,
                            modifier = Modifier.size(20.dp)
                        )
                        CustomText(
                            text = errorMessage,
                            type = CustomTextType.body,
                            color = CustomColor.danger
                        )
                    }
                }
            }

            searchResults.isEmpty() && !isSearching -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = CustomColor.primaryContainer
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_addfriend),
                            contentDescription = null,
                            tint = CustomColor.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        CustomText(
                            text = "친구를 검색해보세요",
                            type = CustomTextType.title,
                            color = CustomColor.primary,
                            textAlign = TextAlign.Center
                        )
                        CustomText(
                            text = "친구의 이메일 또는 친구코드를 입력하여\n새로운 친구를 추가할 수 있습니다",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.primaryDim,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(searchResults) { friend ->
                        Surface(
                            modifier = Modifier.fillMaxWidth()
                                .border(1.dp,CustomColor.gray300, RoundedCornerShape(28.dp)),
                            shape = RoundedCornerShape(24.dp),
                            color = CustomColor.white
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_profile),
                                        contentDescription = null,
                                        tint = CustomColor.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        CustomText(
                                            text = friend.username,
                                            type = CustomTextType.body,
                                            color = CustomColor.textPrimary
                                        )
                                        CustomText(
                                            text = friend.email,
                                            type = CustomTextType.bodySmall,
                                            color = CustomColor.textSecondary
                                        )
                                    }
                                }
                                CustomButton(
                                    text = "추가",
                                    onClick = { onAddFriend(friend.email) },
                                    style = ButtonStyle.Primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

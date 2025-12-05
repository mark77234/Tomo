package com.markoala.tomoandroid.ui.main.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextField
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.ProfileImage
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.main.settings.SettingsContent
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.util.shareInviteCode
import com.markoala.tomoandroid.utils.generateInviteCode

enum class ProfileTab(val label: String) {
    Info("프로필"),
    Settings("설정")
}

@Composable
fun ProfileScreen(
    name: String,
    email: String,
    userId: String,
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit = onSignOut,
    onClose: () -> Unit = {}
) {
    var profileName by remember { mutableStateOf(name) }
    var profileEmail by remember { mutableStateOf(email) }
    val context = LocalContext.current
    val toastManager = LocalToastManager.current

    LaunchedEffect(name) { profileName = name }
    LaunchedEffect(email) { profileEmail = email }

    val inviteCode = generateInviteCode(userId)

    val onCopyInviteCode: () -> Unit = {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("invite_code", inviteCode)
        clipboard.setPrimaryClip(clip)
        toastManager.showSuccess("초대 코드가 복사되었습니다.")
    }

    val onShareInviteCode: () -> Unit = {
        shareInviteCode(context, inviteCode)
    }

    val scrollState = rememberScrollState()
    var selectedTab by remember { mutableStateOf(ProfileTab.Info) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(CustomColor.white),
        containerColor = CustomColor.white,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CustomColor.white)
                    .padding(top = 16.dp, bottom = 16.dp,
                        start = 24.dp,
                        end = 24.dp,
                    )
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)),
            ) {
                CustomButton(
                    text = "닫기",
                    onClick = onClose,
                    style = ButtonStyle.Primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomText(
                    text = "내 프로필",
                    type = CustomTextType.headline,
                    color = CustomColor.textPrimary
                )

                CustomText(
                    text = when (selectedTab) {
                        ProfileTab.Info -> "프로필 정보를 확인하세요"
                        ProfileTab.Settings -> "알림과 계정 설정을 관리하세요"
                    },
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = CustomColor.white,
                    contentColor = CustomColor.primary
                ) {
                    ProfileTab.entries.forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = {
                                CustomText(
                                    text = tab.label,
                                    type = CustomTextType.body,
                                    color = if (selectedTab == tab) CustomColor.primary else CustomColor.gray500
                                )
                            }
                        )
                    }
                }
            }

            when (selectedTab) {
                ProfileTab.Info -> ProfileInfoContent(
                    modifier = Modifier
                        .weight(1f, fill = true)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(scrollState),
                    profileName = profileName,
                    profileEmail = profileEmail,
                    inviteCode = inviteCode,
                    onCopyInviteCode = onCopyInviteCode,
                    onShareInviteCode = onShareInviteCode
                )

                ProfileTab.Settings -> SettingsContent(
                    modifier = Modifier
                        .weight(1f, fill = true),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    onSignOut = onSignOut,
                    onDeleteAccount = onDeleteAccount
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoContent(
    modifier: Modifier = Modifier,
    profileName: String,
    profileEmail: String,
    inviteCode: String,
    onCopyInviteCode: () -> Unit,
    onShareInviteCode: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CustomColor.gray200, shape = RoundedCornerShape(28.dp))
                .background(CustomColor.white, shape = RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.white
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ProfileImage(size = 96.dp)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomText(
                        text = profileName.ifBlank { "이름 없음" },
                        type = CustomTextType.headline,
                        color = CustomColor.primary
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = CustomColor.primary50,
                        modifier = Modifier.clickable { onCopyInviteCode() }
                    ) {
                        CustomText(
                            text = "초대코드: $inviteCode",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CustomColor.gray200, shape = RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.white
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            text = "이름",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.primary
                        )
                    }
                    CustomTextField(
                        value = profileName,
                        onValueChange = {},
                        enabled = false
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            text = "이메일",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.primary
                        )
                    }
                    CustomTextField(
                        value = profileEmail,
                        onValueChange = {},
                        enabled = false
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomButton(
                text = "초대 코드 복사",
                onClick = onCopyInviteCode,
                style = ButtonStyle.Secondary,
                modifier = Modifier.weight(1f)
            )
            CustomButton(
                text = "공유하기",
                onClick = onShareInviteCode,
                style = ButtonStyle.Primary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

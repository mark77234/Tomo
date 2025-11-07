package com.markoala.tomoandroid.ui.main.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.main.settings.components.SettingsToggle
import com.markoala.tomoandroid.ui.theme.CustomColor
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit = {},
) {
    var pushEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    val toastManager = LocalToastManager.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) showDeleteDialog = false },
            containerColor = CustomColor.surface,
            shape = RoundedCornerShape(24.dp),
            title = {
                CustomText(text = "계정 삭제", type = CustomTextType.title, color = CustomColor.textPrimary)
            },
            text = {
                CustomText(
                    text = "정말로 계정을 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.",
                    type = CustomTextType.body,
                    color = CustomColor.textSecondary
                )
            },
            confirmButton = {
                if (isDeleting) {
                    CircularProgressIndicator(color = CustomColor.primary)
                } else {
                    TextButton(onClick = {
                        isDeleting = true
                        coroutineScope.launch {
                            val (success, error) = AuthManager.deleteAccount(context)
                            isDeleting = false
                            showDeleteDialog = false
                            if (success) {
                                toastManager.showSuccess("계정이 삭제되었습니다.")
                                onDeleteAccount()
                            } else {
                                toastManager.showError(error ?: "계정 삭제에 실패했습니다.")
                            }
                        }
                    }) {
                        CustomText(text = "삭제", type = CustomTextType.button, color = CustomColor.danger)
                    }
                }
            },
            dismissButton = {
                if (!isDeleting) {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        CustomText(text = "취소", type = CustomTextType.button, color = CustomColor.textSecondary)
                    }
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.background)
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomText(text = "설정", type = CustomTextType.headline, color = CustomColor.textPrimary)
        CustomText(text = "알림과 테마를 관리하세요", type = CustomTextType.bodySmall, color = CustomColor.textSecondary)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.surface
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                SettingsToggle(
                    title = "푸시 알림",
                    description = "모임 알림과 친구 요청을 받아보세요.",
                    checked = pushEnabled,
                    onCheckedChange = { pushEnabled = it },
                    icon = R.drawable.ic_notification
                )
                SettingsToggle(
                    title = "다크 모드 (개발중)",
                    description = "시스템 테마와 별도로 설정합니다.",
                    checked = darkModeEnabled,
                    onCheckedChange = { darkModeEnabled = it },
                    icon = R.drawable.ic_dark
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        CustomButton(
            text = "로그아웃",
            onClick = {
                onSignOut()
                toastManager.showInfo("로그아웃 되었습니다.")
            },
            style = ButtonStyle.Secondary,
            modifier = Modifier.fillMaxWidth()
        )
        CustomButton(
            text = "계정 삭제",
            onClick = { showDeleteDialog = true },
            style = ButtonStyle.Primary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

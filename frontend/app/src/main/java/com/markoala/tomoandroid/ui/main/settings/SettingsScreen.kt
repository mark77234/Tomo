package com.markoala.tomoandroid.ui.main.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.theme.CustomColor
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit = {},
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    val toastManager = LocalToastManager.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) showDeleteDialog = false },
            containerColor = CustomColor.white,
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
                    CircularProgressIndicator(color = CustomColor.danger)
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
        CustomText(text = "로그인 상태를 관리할 수 있어요", type = CustomTextType.bodySmall, color = CustomColor.textSecondary)

        Spacer(modifier = Modifier.height(8.dp))

        // 계정 관리 섹션
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.primaryContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_setting),
                    contentDescription = null,
                    tint = CustomColor.primary,
                    modifier = Modifier.size(48.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomText(
                        text = "계정 관리",
                        type = CustomTextType.title,
                        color = CustomColor.primary
                    )
                    CustomText(
                        text = "로그아웃하거나 계정을 영구적으로 삭제할 수 있습니다",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.primaryDim
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 로그아웃 카드
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.white
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = null,
                        tint = CustomColor.textSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        CustomText(
                            text = "로그아웃",
                            type = CustomTextType.body,
                            color = CustomColor.textPrimary
                        )
                        CustomText(
                            text = "현재 기기에서 로그아웃합니다",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.textSecondary
                        )
                    }
                }

                CustomButton(
                    text = "로그아웃",
                    onClick = {
                        onSignOut()
                        toastManager.showInfo("로그아웃 되었습니다.")
                    },
                    style = ButtonStyle.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 계정 삭제 카드
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.danger.copy(alpha = 0.05f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = CustomColor.danger,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        CustomText(
                            text = "계정 삭제",
                            type = CustomTextType.body,
                            color = CustomColor.danger
                        )
                        CustomText(
                            text = "모든 데이터가 영구적으로 삭제됩니다",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.danger.copy(alpha = 0.7f)
                        )
                    }
                }

                CustomButton(
                    text = "계정 영구 삭제",
                    onClick = { showDeleteDialog = true },
                    style = ButtonStyle.Primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

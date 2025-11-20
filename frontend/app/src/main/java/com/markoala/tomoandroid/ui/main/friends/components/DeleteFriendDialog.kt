package com.markoala.tomoandroid.ui.main.friends.components

import androidx.compose.runtime.Composable
import com.markoala.tomoandroid.ui.components.DangerDialog

@Composable
fun DeleteFriendDialog(
    friendName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    DangerDialog(
        title = "친구 손절",
        message = "${friendName}과 손절하시겠어요?\n우리 좋았잖아...",
        confirmText = "안녕",
        dismissText = "취소",
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

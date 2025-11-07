package com.markoala.tomoandroid.ui.main.friends.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun DeleteFriendDialog(
    friendName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CustomColor.surface,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        title = {
            CustomText(
                text = "친구 삭제",
                type = CustomTextType.title,
                color = CustomColor.textPrimary
            )
        },
        text = {
            CustomText(
                text = "${friendName}님을 친구 목록에서 삭제하시겠어요?\n이 작업은 되돌릴 수 없습니다.",
                type = CustomTextType.body,
                color = CustomColor.textSecondary
            )
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CustomButton(
                    text = "취소",
                    onClick = onDismiss,
                    style = ButtonStyle.Secondary
                )
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CustomColor.danger,
                        contentColor = CustomColor.white
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(999.dp)
                ) {
                    CustomText(
                        text = "삭제",
                        type = CustomTextType.button,
                        color = CustomColor.white
                    )
                }
            }
        }
    )
}

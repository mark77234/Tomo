package com.markoala.tomoandroid.ui.main.friends.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        containerColor = CustomColor.white,
        shape = RoundedCornerShape(16.dp),
        title = {
            CustomText(
                text = "친구 삭제",
                type = CustomTextType.titleMedium,
                color = CustomColor.black,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                CustomText(
                    text = "'$friendName'님을 친구 목록에서 삭제하시겠습니까?",
                    type = CustomTextType.bodyMedium,
                    color = CustomColor.gray300,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomText(
                    text = "삭제된 친구는 다시 복구할 수 없습니다.",
                    type = CustomTextType.bodyMedium,
                    color = CustomColor.redText,
                    fontSize = 12.sp
                )
            }
        },
        confirmButton = {
            Row {
                OutlinedButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CustomColor.white,
                        contentColor = CustomColor.gray300
                    ),
                    border = BorderStroke(1.dp, CustomColor.gray100),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    CustomText(
                        text = "취소",
                        type = CustomTextType.bodyMedium,
                        color = CustomColor.gray300,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CustomColor.pastelRed
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    CustomText(
                        text = "삭제",
                        type = CustomTextType.bodyMedium,
                        color = CustomColor.darkRed,
                        fontSize = 14.sp
                    )
                }
            }
        }
    )
}

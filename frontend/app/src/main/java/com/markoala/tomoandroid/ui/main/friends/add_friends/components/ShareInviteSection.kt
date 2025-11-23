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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.util.generateInviteCode
import com.markoala.tomoandroid.util.shareInviteCode

@Composable
fun ShareInviteSection( userId: String, onCopy: () -> Unit) {
    val inviteCode = generateInviteCode(userId)
    val context = LocalContext.current
    val onShareInviteCode: () -> Unit = {
        shareInviteCode(context, inviteCode)
    }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 초대 코드 표시 카드
        Surface(
            modifier = Modifier.fillMaxWidth().border(1.dp,CustomColor.gray300, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.white
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_friends),
                    contentDescription = null,
                    tint = CustomColor.primary,
                    modifier = Modifier.size(48.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomText(
                        text = "내 초대 코드",
                        type = CustomTextType.title,
                        color = CustomColor.primary
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),

                    ) {
                        CustomText(
                            text = inviteCode,
                            type = CustomTextType.display,
                            color = CustomColor.primary,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }

        // 설명 카드
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.white
        ) {
            Column(
                modifier = Modifier.padding(top=0.dp,start=16.dp,end=16.dp,bottom=12.dp),
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
                            .background(CustomColor.secondary)
                    )
                    CustomText(
                        text = "코드 공유 방법",
                        type = CustomTextType.body,
                        color = CustomColor.secondary
                    )
                }

                CustomText(
                    text = "친구에게 초대 코드를 공유하면\n간편하게 친구를 추가할 수 있어요!",
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )

                CustomButton(
                    text = "초대 코드 복사",
                    onClick = onCopy,
                    style = ButtonStyle.Primary,
                    modifier = Modifier.fillMaxWidth()
                )
                CustomButton(
                    text = "공유하기",
                    onClick = onShareInviteCode,
                    style = ButtonStyle.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

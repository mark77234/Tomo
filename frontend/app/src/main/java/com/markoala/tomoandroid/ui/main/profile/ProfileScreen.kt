package com.markoala.tomoandroid.ui.main.profile

import android.content.ClipData
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
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
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.util.generateInviteCode

@Composable
fun ProfileScreen(
    name: String,
    email: String,
    userId: String,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
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
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("invite_code", inviteCode)
        clipboard.setPrimaryClip(clip)
        toastManager.showSuccess("초대 코드가 복사되었습니다.")
    }

    val onShareInviteCode: () -> Unit = {
        val deepLink = "tomoapp://invite/$inviteCode"
        val shareText = "Tomo 앱에 초대합니다! 🎉\n초대 코드: $inviteCode\n\n초대하러 가기: $deepLink"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "초대 코드 공유")
        context.startActivity(shareIntent)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CustomColor.background)
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomText(
            text = "내 프로필",
            type = CustomTextType.headline,
            color = CustomColor.textPrimary
        )

        CustomText(
            text = "프로필 정보를 확인하세요",
            type = CustomTextType.bodySmall,
            color = CustomColor.textSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 프로필 카드 - 그라데이션 배경 추가
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),

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

                    // 초대코드를 강조하는 배지 스타일
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = CustomColor.primary.copy(alpha = 0.15f),
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

        // 정보 필드 - 색상 강조
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.white
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
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

        Spacer(modifier = Modifier.weight(1f))

        // 버튼 - 초대코드 복사 및 공유 버튼 추가
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

        CustomButton(
            text = "닫기",
            onClick = onClose,
            style = ButtonStyle.Primary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

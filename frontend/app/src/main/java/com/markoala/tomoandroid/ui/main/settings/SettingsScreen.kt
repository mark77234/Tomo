package com.markoala.tomoandroid.ui.main.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.main.settings.components.SettingsToggle
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit = {},
) {
    var pushEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    val toastManager = LocalToastManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            CustomText(
                text = "설정",
                type = CustomTextType.headline,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CustomColor.gray100, MaterialTheme.shapes.medium),
            colors = CardDefaults.cardColors(
                containerColor = CustomColor.white
            )
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = CustomColor.gray40)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_setting),
                        contentDescription = null,
                        tint = CustomColor.gray300,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    CustomText(text = "앱 설정", type = CustomTextType.title, fontSize = 14.sp)
                }
                HorizontalDivider(
                    modifier = Modifier

                        .fillMaxWidth(),
                    color = CustomColor.gray100
                )
                Box(modifier = Modifier.padding(16.dp)) {
                    SettingsToggle(
                        title = "푸시 알림",
                        description = "모임 알림과 친구 요청을 받아보세요.",
                        checked = pushEnabled,
                        onCheckedChange = { pushEnabled = it },
                        icon = R.drawable.ic_notification
                    )
                }
                HorizontalDivider(
                    modifier = Modifier

                        .fillMaxWidth(),
                    color = CustomColor.gray100
                )
                Box(modifier = Modifier.padding(16.dp)) {
                    SettingsToggle(
                        title = "다크 모드 (개발중)",
                        description = "시스템 테마와 별도로 설정합니다.",
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it },
                        icon = R.drawable.ic_dark
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))


        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = {
                onSignOut()
                toastManager.showInfo("로그아웃 되었습니다.")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = CustomColor.white,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(1.dp, CustomColor.gray100),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            CustomText(
                text = "로그아웃",
                type = CustomTextType.title,
                fontSize = 16.sp,
                color = CustomColor.gray300
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = CustomColor.white,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(1.dp, CustomColor.gray100),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            CustomText(
                text = "계정삭제",
                type = CustomTextType.title,
                fontSize = 16.sp,
                color = CustomColor.gray300
            )
        }
    }
}

package com.markoala.tomoandroid.ui.main.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun SettingsToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: Int? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = CustomColor.gray300
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column {
                CustomText(
                    text = title,
                    type = CustomTextType.body,
                    color = CustomColor.textBody,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                CustomText(
                    text = description,
                    type = CustomTextType.bodySmall,
                    fontSize = 12.sp,
                    color = CustomColor.textSecondary
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CustomColor.white,
                uncheckedThumbColor = CustomColor.white,
                checkedTrackColor = CustomColor.primary,
                uncheckedTrackColor = CustomColor.outline,
                checkedBorderColor = CustomColor.primary,
                uncheckedBorderColor = CustomColor.outline
            )
        )
    }
}

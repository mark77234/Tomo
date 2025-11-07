package com.markoala.tomoandroid.ui.main.home.meeting.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextField
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun StepOneSection(
    title: String,
    description: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CustomColor.surface
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CustomText(text = "모임 제목", type = CustomTextType.bodySmall, color = CustomColor.textSecondary)
                CustomTextField(
                    value = title,
                    onValueChange = onNameChange,
                    placeholder = "모임명을 입력해주세요"
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CustomText(text = "모임 설명", type = CustomTextType.bodySmall, color = CustomColor.textSecondary)
                CustomTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    placeholder = "모임 설명을 적어주세요"
                )
            }
        }
    }
}

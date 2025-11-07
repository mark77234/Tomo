package com.markoala.tomoandroid.ui.main.home.meeting.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    Column(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        CustomText(
            text = "모임 제목",
            type = CustomTextType.body,
            color = CustomColor.black,
            fontSize = 14.sp
        )
        CustomTextField(
            value = title,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "모임명을 입력해주세요"
        )
        CustomText(
            text = "모임 설명",
            type = CustomTextType.body,
            color = CustomColor.black,
            fontSize = 14.sp
        )
        CustomTextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "모임 설명을 적어주세요"
        )
    }
}
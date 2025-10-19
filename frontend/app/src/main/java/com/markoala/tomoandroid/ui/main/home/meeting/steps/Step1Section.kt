package com.markoala.tomoandroid.ui.main.home.meeting.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.CustomTextField

@Composable
fun StepOneSection(
    moimName: String,
    description: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        CustomTextField(
            value = moimName,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "모임명을 입력해주세요"
        )
        CustomTextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "모임 설명을 적어주세요"
        )
    }
}
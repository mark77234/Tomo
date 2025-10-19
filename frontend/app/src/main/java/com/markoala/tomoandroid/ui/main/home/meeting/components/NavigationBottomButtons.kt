package com.markoala.tomoandroid.ui.main.home.meeting.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun NavigationBottomButtons(
    currentStep: Int,
    isLoading: Boolean,
    canGoNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (currentStep > 1) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.weight(1f),
                enabled = true,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = CustomColor.white,
                ),
                border = BorderStroke(1.dp, CustomColor.gray100),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                CustomText(
                    text = "이전", type = CustomTextType.body,
                    color = CustomColor.black
                )
            }
        }

        OutlinedButton(
            onClick = onNext,
            enabled = canGoNext && !isLoading,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = CustomColor.gray50,
                disabledContainerColor = CustomColor.white
            ),
            border = BorderStroke(1.dp, CustomColor.gray200),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            if (isLoading && currentStep == 3) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            CustomText(
                text = if (currentStep < 3) "다음" else "모임 만들기",
                type = CustomTextType.body,
                color = if (canGoNext) CustomColor.black else CustomColor.gray200
            )
        }


    }
}

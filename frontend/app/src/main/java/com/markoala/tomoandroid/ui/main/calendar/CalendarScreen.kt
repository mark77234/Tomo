package com.markoala.tomoandroid.ui.main.calendar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.main.calendar.components.TomoCalendar
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.util.generateCalendarMatrix
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    paddingValues: PaddingValues,
    onEventClick: (Int) -> Unit = {}
) {
    val cardIvory = Color(0xFFFAF7F4)
    val primaryBrown = Color(0xFF9A775A)

    val espressoText = CustomColor.textPrimary
    val secondaryText = Color(0xFF8F8A85)

    val primary200 = CustomColor.primary200
    val primary400 = CustomColor.primary400

    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var selectedDate by remember { mutableStateOf(today) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(CustomColor.white)
            .padding(paddingValues)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TomoCalendar(
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            primaryBrown = primaryBrown,
            espressoText = espressoText,
            secondaryText = secondaryText,
            primary200 = primary200,
            primary400 = primary400,
            cardIvory = cardIvory,
            onPreviousMonth = {
                currentMonth = currentMonth.minusMonths(1)
                selectedDate = currentMonth.atDay(1)
            },
            onNextMonth = {
                currentMonth = currentMonth.plusMonths(1)
                selectedDate = currentMonth.atDay(1)
            },
            onDateSelected = { selectedDate = it }
        )
        Spacer(Modifier.height(20.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .border(1.dp, CustomColor.primary100, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = cardIvory,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CustomText(
                    text = "조금만 기다려 주세요, \n약속 기능이 곧 열릴 거예요!",
                    type = CustomTextType.body,
                    color = CustomColor.primary400,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

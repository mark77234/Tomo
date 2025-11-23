package com.markoala.tomoandroid.ui.main.calendar.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.util.generateCalendarMatrix
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun TomoCalendar(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    primaryBrown: Color,
    espressoText: Color,
    secondaryText: Color,
    primary200: Color,
    primary400: Color,
    cardIvory: Color,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .border(1.dp, CustomColor.primary100, RoundedCornerShape(20.dp)),
        color = cardIvory,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {


            MonthHeader(
                currentMonth = currentMonth,
                primaryBrown = primaryBrown,
                espressoText = espressoText,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
            )

            Spacer(Modifier.height(16.dp))
            WeekdayHeader(secondaryText = secondaryText, primaryBrown = primaryBrown)
            Spacer(Modifier.height(12.dp))


            MonthlyCalendarGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                primaryBrown = primaryBrown,
                primary200 = primary200,
                primary400 = primary400,
                espressoText = espressoText,
                secondaryText = secondaryText,
                onDateSelected = onDateSelected
            )
        }
    }
}

@Composable
private fun MonthHeader(
    currentMonth: YearMonth,
    primaryBrown: Color,
    espressoText: Color,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomText(
            text = "${currentMonth.year}년 ${currentMonth.monthValue}월",
            type = CustomTextType.headline,
            color = espressoText
        )

        Spacer(Modifier.weight(1f))

        HeaderIconLeft(primaryBrown, onPreviousMonth)
        Spacer(Modifier.width(8.dp))
        HeaderIconRight(primaryBrown, onNextMonth)
    }
}

@Composable
private fun HeaderIconLeft(color: Color, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = color.copy(alpha = 0.07f)
    ) {
        IconButton(onClick = onClick) {
            Icon(Icons.Rounded.KeyboardArrowLeft, null, tint = color)
        }
    }
}

@Composable
private fun HeaderIconRight(color: Color, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = color.copy(alpha = 0.07f)
    ) {
        IconButton(onClick = onClick) {
            Icon(Icons.Rounded.KeyboardArrowRight, null, tint = color)
        }
    }
}

@Composable
private fun WeekdayHeader(secondaryText: Color, primaryBrown: Color) {
    val weekdays = listOf("일", "월", "화", "수", "목", "금", "토")

    Row(modifier = Modifier.fillMaxWidth()) {
        weekdays.forEachIndexed { index, day ->
            val color =
                if (index == 0 || index == 6) primaryBrown   // 일·토 → 브라운 강조
                else secondaryText

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CustomText(
                    text = day,
                    color = color,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun MonthlyCalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    primaryBrown: Color,
    primary200: Color,
    primary400: Color,
    espressoText: Color,
    secondaryText: Color,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val weeks = remember(currentMonth) { generateCalendarMatrix(currentMonth) }

    Column(Modifier.fillMaxWidth()) {
        weeks.forEach { week ->
            Row(Modifier.fillMaxWidth()) {
                week.forEachIndexed { index, date ->
                    val inMonth = date.month == currentMonth.month
                    val selected = date == selectedDate
                    val isToday = date == today
                    val isWeekend = index == 0 || index == 6     // 일=0, 토=6

                    CalendarDayCell(
                        date = date,
                        isCurrentMonth = inMonth,
                        isSelected = selected,
                        isToday = isToday,
                        isWeekend = isWeekend,
                        primaryBrown = primaryBrown,
                        primary200 = primary200,
                        primary400 = primary400,
                        espressoText = espressoText,
                        secondaryText = secondaryText,
                        onClick = { if (inMonth) onDateSelected(date) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.CalendarDayCell(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    isWeekend: Boolean,
    primaryBrown: Color,
    primary200: Color,
    primary400: Color,
    espressoText: Color,
    secondaryText: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && isCurrentMonth) 1.05f else 1f,
        label = "scale"
    )

    val textColor =
        when {
            isSelected -> Color.White
            !isCurrentMonth -> secondaryText.copy(alpha = 0.35f)
            isToday -> CustomColor.primary
            isWeekend -> primary400     // 토/일 → 강조색
            else -> espressoText
        }

    val backgroundModifier =
        when {
            isSelected -> Modifier.size(36.dp).background(primaryBrown, CircleShape)
            isToday -> Modifier.size(36.dp).background(primary200, CircleShape)
            else -> Modifier
        }

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .clickable(
                    enabled = isCurrentMonth,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = backgroundModifier,
                contentAlignment = Alignment.Center
            ) {
                CustomText(
                    text = "${date.dayOfMonth}",
                    type = CustomTextType.body,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}



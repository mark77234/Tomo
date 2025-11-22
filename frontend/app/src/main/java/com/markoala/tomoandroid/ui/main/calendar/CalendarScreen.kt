package com.markoala.tomoandroid.ui.main.calendar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.markoala.tomoandroid.ui.theme.CustomColor
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    paddingValues: PaddingValues,
    onEventClick: (Int) -> Unit = {}
) {
    val backgroundBeige = Color(0xFFF7F1EC)
    val cardIvory = Color(0xFFFAF7F4)
    val primaryBrown = Color(0xFF9A775A)
    val espressoText = CustomColor.textPrimary
    val secondaryText = Color(0xFF8F8A85)

    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var selectedDate by remember { mutableStateOf(today) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.white)
            .padding(paddingValues)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            color = cardIvory,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                MonthHeader(
                    currentMonth = currentMonth,
                    primaryBrown = primaryBrown,
                    espressoText = espressoText,
                    onPreviousMonth = {
                        currentMonth = currentMonth.minusMonths(1)
                        selectedDate = currentMonth.atDay(1)
                    },
                    onNextMonth = {
                        currentMonth = currentMonth.plusMonths(1)
                        selectedDate = currentMonth.atDay(1)
                    }
                )

                Spacer(Modifier.height(16.dp))
                WeekdayHeader(secondaryText = secondaryText, primaryBrown = primaryBrown)
                Spacer(Modifier.height(12.dp))

                MonthlyCalendarGrid(
                    currentMonth = currentMonth,
                    selectedDate = selectedDate,
                    primaryBrown = primaryBrown,
                    espressoText = espressoText,
                    secondaryText = secondaryText,
                    onDateSelected = { selectedDate = it }
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ⭐ 하단 약속 기능 추가 예정 박스
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            shape = RoundedCornerShape(20.dp),
            color = cardIvory,
            shadowElevation = 2.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CustomText(
                    text = "조금만 기다려 주세요, \n약속 기능이 곧 열릴 거예요!",
                    type = CustomTextType.body,
                    color = espressoText,
                    textAlign = TextAlign.Center
                )
            }
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

// 왼쪽 이동
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

// 오른쪽 이동 (⭐ 올바른 아이콘 적용)
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
            val color = if (index == 0) primaryBrown else secondaryText

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
    espressoText: Color,
    secondaryText: Color,
    onDateSelected: (LocalDate) -> Unit
) {
    val weeks = remember(currentMonth) { generateCalendarMatrix(currentMonth) }

    Column(Modifier.fillMaxWidth()) {
        weeks.forEach { week ->
            Row(Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    val inMonth = date.month == currentMonth.month
                    val selected = date == selectedDate

                    CalendarDayCell(
                        date = date,
                        isCurrentMonth = inMonth,
                        isSelected = selected,
                        primaryBrown = primaryBrown,
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
    primaryBrown: Color,
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
            else -> espressoText
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

            // 선택된 날짜 동그라미
            Box(
                modifier = Modifier
                    .then(
                        if (isSelected)
                            Modifier
                                .size(36.dp)
                                .background(primaryBrown, CircleShape)
                        else Modifier
                    ),
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

private fun generateCalendarMatrix(month: YearMonth): List<List<LocalDate>> {
    val first = month.atDay(1)
    val index = when (first.dayOfWeek) {
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
        DayOfWeek.SUNDAY -> 0
    }
    val startDate = first.minusDays(index.toLong())
    return (0 until 42).map { startDate.plusDays(it.toLong()) }.chunked(7)
}

package com.markoala.tomoandroid.ui.main.calendar.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.ui.main.calendar.model.CalendarEvent
import com.markoala.tomoandroid.ui.main.calendar.model.CalendarEventType
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.utils.generateCalendarMatrix
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun TomoCalendar(
    events: Map<LocalDate, List<CalendarEvent>>,
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onDayClick: (LocalDate, List<CalendarEvent>) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .border(1.dp, CustomColor.primary100, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFFAF7F4)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {

            MonthHeader(
                currentMonth = currentMonth,
                onPreviousMonth = {
                    onPreviousMonth()
                },
                onNextMonth = {
                    onNextMonth()
                }
            )

            Spacer(Modifier.height(16.dp))
            WeekdayHeader()

            Spacer(Modifier.height(12.dp))

            Crossfade(targetState = currentMonth) { animatedMonth ->
                CalendarContentGrid(
                    currentMonth = animatedMonth,
                    selectedDate = selectedDate,
                    events = events,
                    onDateSelected = onDateSelected,
                    onDayClick = onDayClick,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth
                )
            }
        }
    }
}

@Composable
private fun MonthHeader(
    currentMonth: YearMonth,
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
            color = CustomColor.primaryDim
        )

        Spacer(Modifier.weight(1f))

        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Rounded.KeyboardArrowLeft, contentDescription = null)
        }
        Spacer(Modifier.width(4.dp))
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Rounded.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
private fun WeekdayHeader() {
    val weekdays = listOf("일", "월", "화", "수", "목", "금", "토")

    Row(Modifier.fillMaxWidth()) {
        weekdays.forEachIndexed { idx, day ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CustomText(
                    text = day,
                    color = if (idx == 0 || idx == 6) CustomColor.primaryDim else CustomColor.gray300,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CalendarContentGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    events: Map<LocalDate, List<CalendarEvent>>,
    onDateSelected: (LocalDate) -> Unit,
    onDayClick: (LocalDate, List<CalendarEvent>) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    var totalDragX by remember { mutableStateOf(0f) }

    // -----------------------------------
    // 2) 스와이프 제스처 적용
    // -----------------------------------
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        // 스와이프 종료 후 방향 기반으로 월 이동 결정
                        if (totalDragX < -60) {
                            onNextMonth()
                        } else if (totalDragX > 60) {
                            onPreviousMonth()
                        }

                        totalDragX = 0f  // 초기화
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        totalDragX += dragAmount
                    }
                )
            }

    ) {
        val today = LocalDate.now()
        val weeks = remember(currentMonth) { generateCalendarMatrix(currentMonth) }

        weeks.forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(vertical = 6.dp)
            ) {
                week.forEachIndexed { index, date ->

                    val inMonth = date.month == currentMonth.month
                    val isToday = today == date
                    val isWeekend = index == 0 || index == 6

                    CalendarDayCell(
                        date = date,
                        isCurrentMonth = inMonth,
                        isToday = isToday,
                        isWeekend = isWeekend,
                        events = events[date],
                        onClick = {
                            when {
                                inMonth -> {
                                    onDateSelected(date)
                                    onDayClick(date, events[date] ?: emptyList())
                                }
                                date.isBefore(currentMonth.atDay(1)) -> {
                                    onPreviousMonth()
                                }
                                date.isAfter(currentMonth.atEndOfMonth()) -> {
                                    onNextMonth()
                                }
                            }
                        }
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
    isToday: Boolean,
    isWeekend: Boolean,
    events: List<CalendarEvent>?,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        if (isPressed) 1.05f else 1f,
        label = ""
    )

    val alpha = if (isCurrentMonth) 1f else 0.3f

    Column(
        modifier = Modifier
            .weight(1f)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                alpha = alpha
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 날짜 원
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    if (isToday) CustomColor.primary300 else Color.Transparent,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            CustomText(
                text = "${date.dayOfMonth}",
                type = CustomTextType.body,
                color = when {
                    isToday -> CustomColor.white
                    isWeekend -> CustomColor.primary400
                    else -> CustomColor.textPrimary
                }
            )
        }

        // 뱃지 3개까지
        events?.take(3)?.forEach { item ->
            Spacer(Modifier.height(3.dp))
            val badgeColor = if (item.type == CalendarEventType.PROMISE) CustomColor.primary400 else CustomColor.primary100
            val textColor = if (item.type == CalendarEventType.PROMISE) CustomColor.white else CustomColor.primary400
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 3.dp)
                    .background(badgeColor, RoundedCornerShape(3.dp)),
                contentAlignment = Alignment.Center
            ) {
                CustomText(
                    text = item.title.take(4),
                    color = textColor,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

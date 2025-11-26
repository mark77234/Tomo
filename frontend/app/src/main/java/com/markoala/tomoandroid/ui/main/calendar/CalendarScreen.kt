package com.markoala.tomoandroid.ui.main.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.data.model.moim.MoimListDTO
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.MorphingDots
import com.markoala.tomoandroid.ui.main.calendar.components.TomoCalendar
import com.markoala.tomoandroid.ui.main.meeting.MeetingViewModel
import com.markoala.tomoandroid.ui.theme.CustomColor
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    paddingValues: PaddingValues,
    meetingViewModel: MeetingViewModel = viewModel(),
    onEventClick: (Int) -> Unit = {}
) {
    val cardIvory = Color(0xFFFAF7F4)

    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var selectedDate by remember { mutableStateOf(today) }

    val meetings by meetingViewModel.meetings.collectAsState()
    val isLoading by meetingViewModel.isLoading.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    var dailySchedules by remember { mutableStateOf<List<MoimListDTO>?>(null) }


    val eventMap = remember(meetings) {
        meetings
            .mapNotNull { moim ->
                runCatching {
                    val date = LocalDate.parse(moim.createdAt.substring(0, 10))
                    date to moim
                }.getOrNull()
            }
            .groupBy({ it.first }, { it.second })
    }


    LaunchedEffect(eventMap) {
        println("🔥 eventMap =")
        eventMap.forEach { (date, list) ->
            println("$date -> ${list.map { it.title }}")
        }
    }



    // Lifecycle: 화면 복귀 시 데이터 다시 불러오기
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                meetingViewModel.fetchMeetings()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // 🔥 로딩 다이얼로그 표시
    if (isLoading) {
        MorphingDots()
    }

    if (dailySchedules != null) {

        Dialog(onDismissRequest = { dailySchedules = null }) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .heightIn(max = 500.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                tonalElevation = 4.dp
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    // -------------------------
                    // Header — 일정 제목
                    // -------------------------
                    CustomText(
                        text = "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일 일정",
                        type = CustomTextType.headline,
                        color = CustomColor.primary400
                    )

                    Spacer(Modifier.height(16.dp))

                    // -------------------------
                    // Scrollable Content
                    // -------------------------
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        dailySchedules!!.forEach { schedule ->

                            // 일정 타입 (현재는 모두 모임 생성으로 표시)
                            val scheduleType = "최초 생성"

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable {
                                        dailySchedules = null
                                        onEventClick(schedule.moimId)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = CustomColor.primary50,   // 너무 튀지 않는 배경색
                                tonalElevation = 1.dp
                            ) {

                                Column(
                                    modifier = Modifier
                                        .padding(14.dp)
                                ) {

                                    // ------------------------
                                    // 타입 배지
                                    // ------------------------
                                    Box(
                                        modifier = Modifier
                                            .background(CustomColor.primary100, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        CustomText(
                                            text = scheduleType,
                                            color = CustomColor.primary
                                        )
                                    }

                                    Spacer(Modifier.height(6.dp))

                                    // ------------------------
                                    // 제목
                                    // ------------------------
                                    CustomText(
                                        text = schedule.title,
                                        type = CustomTextType.body,
                                        color = CustomColor.textPrimary
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    // ------------------------
                                    // 보조 정보 (예: 생성일, 추후 장소, 메모 등)
                                    // ------------------------
                                    CustomText(
                                        text = "설명:  ${schedule.description}",
                                        type= CustomTextType.bodySmall,
                                        color = CustomColor.gray500
                                    )
                                }
                            }
                        }

                    }

                    Spacer(Modifier.height(10.dp))

                    // -------------------------
                    // Footer — 닫기 버튼
                    // -------------------------
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CustomColor.primary100,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable { dailySchedules = null }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            CustomText(
                                text = "닫기",
                                type = CustomTextType.body,
                                color = CustomColor.primary400
                            )
                        }
                    }
                }
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(CustomColor.white)
            .padding(paddingValues)
            .padding(top=24.dp,end=8.dp, start=8.dp,bottom=24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TomoCalendar(
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            onPreviousMonth = {
                currentMonth = currentMonth.minusMonths(1)
                selectedDate = currentMonth.atDay(1)
            },
            onNextMonth = {
                currentMonth = currentMonth.plusMonths(1)
                selectedDate = currentMonth.atDay(1)
            },
            onDateSelected = { selectedDate = it },
            events = eventMap,
            onDayClick = { date, schedules ->
                selectedDate = date
                dailySchedules = schedules
            }
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

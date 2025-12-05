package com.markoala.tomoandroid.ui.main.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.markoala.tomoandroid.data.api.PromiseApiService
import com.markoala.tomoandroid.data.model.MoimListDTO
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.MorphingDots
import com.markoala.tomoandroid.ui.main.calendar.components.TomoCalendar
import com.markoala.tomoandroid.ui.main.calendar.model.CalendarEvent
import com.markoala.tomoandroid.ui.main.calendar.model.CalendarEventType
import com.markoala.tomoandroid.ui.main.meeting.MeetingViewModel
import com.markoala.tomoandroid.ui.theme.CustomColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    paddingValues: PaddingValues,
    meetingViewModel: MeetingViewModel = viewModel(),
    onEventClick: (Int) -> Unit = {},
    onAddPromiseClick: (LocalDate) -> Unit = {}
) {
    val cardIvory = Color(0xFFFAF7F4)

    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var selectedDate by remember { mutableStateOf(today) }

    val meetings by meetingViewModel.meetings.collectAsState()
    val isLoading by meetingViewModel.isLoading.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val meetingsState = rememberUpdatedState(meetings)
    var dailySchedules by remember { mutableStateOf<List<CalendarEvent>?>(null) }
    var promiseEvents by remember { mutableStateOf<List<CalendarEvent>>(emptyList()) }
    var isPromiseLoading by remember { mutableStateOf(false) }
    var hasFetchedPromises by remember { mutableStateOf(false) }

    val meetingEvents = remember(meetings) {
        meetings.mapNotNull { moim ->
            runCatching {
                val date = LocalDate.parse(moim.createdAt.substring(0, 10))
                CalendarEvent(
                    id = "moim-${moim.moimId}-${moim.title}-${moim.createdAt}",
                    date = date,
                    title = moim.title,
                    description = moim.description,
                    type = CalendarEventType.MOIM,
                    moimId = moim.moimId,
                    moimTitle = moim.title
                )
            }.getOrNull()
        }
    }

    LaunchedEffect(meetings, hasFetchedPromises) {
        if (hasFetchedPromises || meetings.isEmpty()) return@LaunchedEffect
        isPromiseLoading = true
        promiseEvents = runCatching { fetchPromiseEvents(meetings) }.getOrDefault(emptyList())
        hasFetchedPromises = true
        isPromiseLoading = false
    }

    val eventMap by remember(meetingEvents, promiseEvents) {
        derivedStateOf { (meetingEvents + promiseEvents).groupBy { it.date } }
    }


    // Lifecycle: 화면 복귀 시 데이터 다시 불러오기
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (meetingsState.value.isEmpty()) {
                    meetingViewModel.fetchMeetings()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // 🔥 로딩 다이얼로그 표시
    if (isLoading || isPromiseLoading) {
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomText(
                            text = "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일 일정",
                            type = CustomTextType.headline,
                            color = CustomColor.primary400
                        )
                        CustomButton(
                            text = "약속 추가",
                            style = ButtonStyle.Primary,
                            onClick = {
                                dailySchedules = null
                                onAddPromiseClick(selectedDate)
                            },
                            modifier = Modifier.height(50.dp)
                        )
                    }

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

                            val isPromise = schedule.type == CalendarEventType.PROMISE
                            val badgeLabel = if (isPromise) "약속" else "모임"
                            val secondaryText = if (isPromise) {
                                listOfNotNull(schedule.moimTitle, schedule.promiseTime).joinToString(" · ")
                            } else {
                                schedule.description
                            }
                            val placeText = if (isPromise && !schedule.place.isNullOrBlank()) {
                                schedule.place
                            } else null

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .let { base ->
                                        if (!isPromise && schedule.moimId != null) {
                                            base.clickable {
                                                dailySchedules = null
                                                onEventClick(schedule.moimId)
                                            }
                                        } else base
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isPromise) CustomColor.primary100 else CustomColor.primary50,
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
                                            .background(CustomColor.primary200, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        CustomText(
                                            text = badgeLabel,
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
                                    // 보조 정보
                                    // ------------------------
                                    secondaryText?.let {
                                        CustomText(
                                            text = it,
                                            type= CustomTextType.bodySmall,
                                            color = CustomColor.gray500
                                        )
                                    }
                                    placeText?.let {
                                        Spacer(Modifier.height(2.dp))
                                        CustomText(
                                            text = "장소: $it",
                                            type = CustomTextType.bodySmall,
                                            color = CustomColor.gray500
                                        )
                                    }
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
                    text = "날짜를 눌러 모임과 약속을 확인하고 관리하세요.",
                    type = CustomTextType.body,
                    color = CustomColor.primary400,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private suspend fun fetchPromiseEvents(meetings: List<MoimListDTO>): List<CalendarEvent> = withContext(Dispatchers.IO) {
    val formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
    val collected = mutableListOf<CalendarEvent>()
    meetings.forEach { moim ->
        runCatching {
            PromiseApiService.getPromisesList(moim.title).awaitResponse()
        }.onSuccess { response ->
            if (response.isSuccessful) {
                response.body()?.data.orEmpty().forEach { promise ->
                    val date = runCatching { LocalDate.parse(promise.promiseDate.take(10), formatter) }.getOrNull()
                    if (date != null) {
                        collected += CalendarEvent(
                            id = "promise-${moim.moimId}-${promise.promiseName}-${promise.promiseDate}-${promise.promiseTime}",
                            date = date,
                            title = promise.promiseName,
                            description = moim.title,
                            type = CalendarEventType.PROMISE,
                            moimId = moim.moimId,
                            promiseTime = promise.promiseTime,
                            place = promise.resolvedLocation,
                            moimTitle = moim.title
                        )
                    }
                }
            }
        }
    }
    collected
}

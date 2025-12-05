package com.markoala.tomoandroid.ui.main.calendar.promise

import android.app.TimePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.data.api.GeocodeAddress
import com.markoala.tomoandroid.data.model.MoimListDTO
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomBack
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextField
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.main.map.MapScreen
import com.markoala.tomoandroid.ui.main.map.map_search.MapSearchScreen
import com.markoala.tomoandroid.ui.theme.CustomColor
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun CreatePromiseScreen(
    paddingValues: PaddingValues,
    selectedDate: LocalDate,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit = {},
    initialAddress: GeocodeAddress? = null,
    initialQuery: String? = null
) {
    val viewModel: CreatePromiseViewModel = viewModel()
    val moims by viewModel.moims.collectAsState()
    val selectedMoim by viewModel.selectedMoim.collectAsState()
    val promiseName by viewModel.promiseName.collectAsState()
    val promiseDate by viewModel.promiseDate.collectAsState()
    val promiseTime by viewModel.promiseTime.collectAsState()
    val place by viewModel.place.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val selectedQuery by viewModel.selectedQuery.collectAsState()
    val isFetchingMoims by viewModel.isFetchingMoims.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val toastManager = LocalToastManager.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var showMapSearch by remember { mutableStateOf(false) }
    fun GeocodeAddress.displayLabel(): String {
        return listOfNotNull(name, roadAddress, jibunAddress, englishAddress)
            .firstOrNull { it.isNotBlank() }
            ?: "선택한 장소"
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchLeaderMoims()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(selectedDate) {
        viewModel.setSelectedDate(selectedDate)
    }

    LaunchedEffect(initialAddress, initialQuery) {
        if (initialAddress != null) {
            val label = initialQuery?.takeIf { it.isNotBlank() } ?: initialAddress.displayLabel()
            viewModel.updateLocation(label, initialAddress)
        }
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess == true) {
            toastManager.showSuccess("약속이 생성됐어요!")
            viewModel.consumeSuccess()
            onSuccess()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { toastManager.showWarning(it) }
    }

    BackHandler(enabled = showMapSearch) {
        showMapSearch = false
    }

    if (showMapSearch) {
        MapSearchScreen(
            paddingValues = paddingValues,
            initialQuery = selectedQuery,
            onBackClick = { showMapSearch = false },
            onSelect = { query, address ->
                viewModel.updateLocation(query, address)
                showMapSearch = false
            }
        )
        return
    }

    val isFormReady = selectedMoim != null &&
        promiseName.isNotBlank() &&
        promiseTime != null &&
        !place.isNullOrBlank()

    val friendlyDate = remember(selectedDate) { "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일" }

    fun openTimePicker() {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val initial = promiseTime?.let {
            runCatching { LocalTime.parse(it, formatter) }.getOrElse { LocalTime.now() }
        } ?: LocalTime.now()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                viewModel.setPromiseTime(LocalTime.of(hour, minute))
            },
            initial.hour,
            initial.minute,
            true
        ).show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.white)
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .padding(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CustomBack(onClick = onBackClick)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    CustomText(
                        text = "약속 만들기",
                        type = CustomTextType.headline,
                        color = CustomColor.textPrimary
                    )
                    CustomText(
                        text = "$friendlyDate 에 약속을 등록해요.",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.textSecondary
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = CustomColor.primary50
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomText(
                        text = "선택한 날짜",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.textSecondary
                    )
                    CustomText(
                        text = promiseDate.ifBlank { selectedDate.toString() },
                        type = CustomTextType.title,
                        color = CustomColor.primary400
                    )
                    CustomText(
                        text = "시간은 아래에서 선택할 수 있어요.",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.gray500
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CustomText(
                    text = "모임 선택",
                    type = CustomTextType.title,
                    color = CustomColor.textPrimary
                )
                CustomText(
                    text = "내가 모임장인 모임만 보여요.",
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )
                if (isFetchingMoims) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = CustomColor.background
                    ) {
                        CustomText(
                            text = "모임 목록을 불러오는 중이에요...",
                            type = CustomTextType.body,
                            color = CustomColor.textSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else if (moims.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = CustomColor.gray100
                    ) {
                        CustomText(
                            text = "모임장인 모임이 없어요. 모임을 만든 뒤 약속을 생성할 수 있어요.",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.textSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        moims.forEach { moim ->
                            MoimChoiceCard(
                                moim = moim,
                                selected = moim.moimId == selectedMoim?.moimId,
                                onSelect = { viewModel.selectMoim(it) }
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CustomText(
                    text = "약속 정보",
                    type = CustomTextType.title,
                    color = CustomColor.textPrimary
                )
                CustomTextField(
                    value = promiseName,
                    onValueChange = { viewModel.updatePromiseName(it) },
                    placeholder = "약속 이름을 입력하세요 (예: 주간 스터디)",
                    supportingText = "모임원들이 알아보기 쉬운 이름을 추천해요."
                )
                InfoField(
                    label = "약속 날짜",
                    value = promiseDate.ifBlank { selectedDate.toString() }
                )
                InfoField(
                    label = "약속 시간",
                    value = promiseTime ?: "시간을 선택하세요",
                    onClick = { openTimePicker() }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CustomText(
                    text = "장소 선택",
                    type = CustomTextType.title,
                    color = CustomColor.textPrimary
                )
                CustomText(
                    text = "카카오 지도에서 검색 후 위치를 선택하세요.",
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )
                CustomButton(
                    text = "장소 검색하기",
                    onClick = { showMapSearch = true },
                    style = ButtonStyle.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = CustomColor.white,
                    shadowElevation = 2.dp
                ) {
                    MapScreen(
                        paddingValues = PaddingValues(0.dp),
                        selectedAddress = selectedAddress,
                        selectedQuery = selectedQuery,
                        onSearchClick = { showMapSearch = true },
                        interactive = false,
                        isPromise = false,
                        showSearchOverlay = false
                    )
                }
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CustomColor.gray100
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CustomText(
                            text = "선택된 장소",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.textSecondary
                        )
                        CustomText(
                            text = place ?: "아직 장소를 선택하지 않았어요.",
                            type = CustomTextType.body,
                            color = if (place.isNullOrBlank()) CustomColor.gray500 else CustomColor.textPrimary
                        )
                    }
                }
            }

            errorMessage?.let {
                CustomText(
                    text = it,
                    type = CustomTextType.bodySmall,
                    color = CustomColor.danger
                )
            }
        }

        CustomButton(
            text = if (isSubmitting) "약속 생성 중..." else "약속 생성하기",
            onClick = { viewModel.createPromise() },
            enabled = isFormReady && !isSubmitting,
            style = ButtonStyle.Primary,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun MoimChoiceCard(
    moim: MoimListDTO,
    selected: Boolean,
    onSelect: (MoimListDTO) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onSelect(moim) },
        shape = RoundedCornerShape(14.dp),
        color = if (selected) CustomColor.primary100 else CustomColor.white,
        tonalElevation = if (selected) 2.dp else 0.dp,
        border = if (selected) null else BorderStroke(1.dp, CustomColor.outline)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                CustomText(
                    text = moim.title,
                    type = CustomTextType.body,
                    color = CustomColor.textPrimary
                )
                CustomText(
                    text = if (selected) "선택됨" else "",
                    type = CustomTextType.bodySmall,
                    color = CustomColor.primary400
                )
            }
            CustomText(
                text = moim.description,
                type = CustomTextType.bodySmall,
                color = CustomColor.textSecondary
            )
        }
    }
}

@Composable
private fun InfoField(
    label: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    val modifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    } else {
        Modifier.fillMaxWidth()
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = CustomColor.background,
        border = BorderStroke(1.dp, CustomColor.outline)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CustomText(
                text = label,
                type = CustomTextType.bodySmall,
                color = CustomColor.textSecondary
            )
            CustomText(
                text = value,
                type = CustomTextType.body,
                color = CustomColor.textPrimary
            )
        }
    }
}

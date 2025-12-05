package com.markoala.tomoandroid.ui.main.meeting.meeting_detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.data.api.PromiseApiService
import com.markoala.tomoandroid.data.model.PromiseResponseDTO
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomBack
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LoadingDialog
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.utils.formatTimeWithoutSeconds
import com.markoala.tomoandroid.utils.parseIsoToKoreanDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class MeetingPromiseListViewModel : ViewModel() {
    private val _promises = MutableStateFlow<List<PromiseResponseDTO>>(emptyList())
    val promises: StateFlow<List<PromiseResponseDTO>> = _promises

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchPromises(moimName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = PromiseApiService.getPromisesList(moimName).awaitResponse()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        _promises.value = body.data.orEmpty()
                    } else {
                        _promises.value = emptyList()
                        _errorMessage.value = body?.message ?: "약속 목록을 불러오지 못했어요."
                    }
                } else {
                    _promises.value = emptyList()
                    _errorMessage.value = "서버 오류: ${response.code()}"
                }
            } catch (e: Exception) {
                _promises.value = emptyList()
                _errorMessage.value = "네트워크 오류가 발생했어요."
            } finally {
                _isLoading.value = false
            }
        }
    }
}

@Composable
fun MeetingPromiseListScreen(
    moimId: Int,
    moimName: String,
    isLeader: Boolean = false,
    onBackClick: () -> Unit,
    onCreatePromiseClick: (moimId: Int, moimName: String) -> Unit = { _, _ -> },
    viewModel: MeetingPromiseListViewModel = viewModel()
)
{
    val promises by viewModel.promises.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(moimName) {
        viewModel.fetchPromises(moimName)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.white)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        if (isLoading) {
            LoadingDialog()
        }

        val bottomPadding = if (isLeader) 100.dp else 24.dp

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = 16.dp,
                bottom = bottomPadding
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomBack(onClick = onBackClick)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        CustomText(
                            text = "약속 조회",
                            type = CustomTextType.headline,
                            color = CustomColor.textPrimary
                        )
                        CustomText(
                            text = "$moimName 모임의 약속을 확인하세요.",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.textSecondary
                        )
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = CustomColor.primary50,
                    border = BorderStroke(1.dp, CustomColor.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomText(
                            text = moimName,
                            type = CustomTextType.title,
                            color = CustomColor.primary
                        )
                        CustomText(
                            text = "모임의 예정된 약속들을 한 눈에 볼 수 있어요.",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.textSecondary
                        )
                    }
                }
            }

            when {
                errorMessage != null -> {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = CustomColor.gray100
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CustomText(
                                    text = errorMessage ?: "약속 목록을 불러오지 못했어요.",
                                    type = CustomTextType.body,
                                    color = CustomColor.textSecondary
                                )
                                CustomButton(
                                    text = "다시 시도",
                                    onClick = { viewModel.fetchPromises(moimName) },
                                    style = ButtonStyle.Secondary
                                )
                            }
                        }
                    }
                }

                promises.isEmpty() -> {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = CustomColor.background,
                            border = BorderStroke(1.dp, CustomColor.outline)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CustomText(
                                    text = "아직 등록된 약속이 없어요.",
                                    type = CustomTextType.body,
                                    color = CustomColor.textSecondary
                                )
                                CustomButton(
                                    text = "새로고침",
                                    onClick = { viewModel.fetchPromises(moimName) },
                                    style = ButtonStyle.Secondary
                                )
                            }
                        }
                    }
                }

                else -> {
                    items(
                        items = promises,
                        key = { promise ->
                            val safeLocation = promise.resolvedLocation
                            "${promise.promiseName}-${promise.promiseDate}-${promise.promiseTime}-${safeLocation}"
                        }
                    ) { promise ->
                        PromiseItemCard(promise)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }

        if (isLeader) {
            CustomButton(
                text = "약속 잡기",
                onClick = { onCreatePromiseClick(moimId, moimName) },
                style = ButtonStyle.Primary,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        }
    }
}

@Composable
private fun PromiseItemCard(promise: PromiseResponseDTO) {
    val formattedDate = parseIsoToKoreanDate(promise.promiseDate)
    val placeText = promise.resolvedLocation.ifBlank { "장소 미정" }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = CustomColor.white,
        border = BorderStroke(1.dp, CustomColor.outline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CustomText(
                        text = promise.promiseName,
                        type = CustomTextType.title,
                        color = CustomColor.textPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CustomColor.primary50,
                    border = BorderStroke(1.dp, CustomColor.outline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_time),
                            contentDescription = null,
                            tint = CustomColor.primaryDim,
                            modifier = Modifier.size(16.dp)
                        )
                        CustomText(
                            text = formatTimeWithoutSeconds(promise.promiseTime),
                            type = CustomTextType.bodySmall,
                            color = CustomColor.primaryDim
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PromiseMetaChip(
                    icon = R.drawable.ic_calendar,
                    text = formattedDate
                )
                PromiseMetaChip(
                    icon = R.drawable.ic_location,
                    text = placeText
                )
            }

        }
    }
}

@Composable
private fun PromiseMetaChip(
    icon: Int,
    text: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CustomColor.primary50,
        border = BorderStroke(1.dp, CustomColor.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = CustomColor.white,
                border = BorderStroke(1.dp, CustomColor.outline)
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = CustomColor.primaryDim,
                    modifier = Modifier.padding(6.dp).size(14.dp)
                )
            }
            CustomText(
                text = text,
                type = CustomTextType.bodySmall,
                color = CustomColor.textSecondary
            )
        }
    }
}

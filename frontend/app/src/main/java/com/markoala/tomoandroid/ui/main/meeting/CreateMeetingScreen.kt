package com.markoala.tomoandroid.ui.main.meeting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.data.model.friends.FriendProfile
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun CreateMeetingScreen(
    paddingValues: PaddingValues,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit = {}
) {
    val viewModel: CreateMeetingViewModel = viewModel()
    val moimName by viewModel.moimName.collectAsState()
    val description by viewModel.description.collectAsState()
    val friends by viewModel.friends.collectAsState()
    val selectedEmails by viewModel.selectedEmails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var currentStep by rememberSaveable { mutableStateOf(1) }

    LaunchedEffect(isSuccess) {
        if (isSuccess == true) {
            onSuccess()
            viewModel.consumeSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        MeetingHeader(onBackClick = onBackClick)
        Spacer(modifier = Modifier.height(16.dp))
        StepIndicator(currentStep = currentStep)
        Spacer(modifier = Modifier.height(24.dp))

        when (currentStep) {
            1 -> StepOneSection(
                moimName = moimName,
                description = description,
                onNameChange = {
                    viewModel.moimName.value = it
                    viewModel.clearError()
                },
                onDescriptionChange = {
                    viewModel.description.value = it
                    viewModel.clearError()
                }
            )

            2 -> StepTwoSection(
                friends = friends,
                selectedEmails = selectedEmails,
                onToggleEmail = { viewModel.toggleEmail(it) }
            )

            3 -> StepThreeSection(
                moimName = moimName,
                description = description,
                selectedFriends = friends.filter { selectedEmails.contains(it.email) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage != null) {
            CustomText(
                text = errorMessage ?: "",
                type = CustomTextType.bodySmall,
                color = CustomColor.gray300
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        NavigationButtons(
            currentStep = currentStep,
            isLoading = isLoading,
            canGoNext = when (currentStep) {
                1 -> moimName.isNotBlank() && description.isNotBlank()
                else -> true
            },
            onPrevious = {
                if (currentStep > 1) {
                    currentStep -= 1
                    viewModel.clearError()
                }
            },
            onNext = {
                if (currentStep < 3) {
                    currentStep += 1
                    viewModel.clearError()
                } else {
                    viewModel.createMoim()
                }
            }
        )
    }
}

@Composable
private fun MeetingHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomText(
            text = "모임 생성",
            type = CustomTextType.headlineLarge,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = CustomColor.gray100,
                    shape = RoundedCornerShape(32.dp)
                )
                .clickable { onBackClick() },
            shape = RoundedCornerShape(32.dp),
            color = CustomColor.white
        ) {
            Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)) {
                CustomText(
                    text = "목록보기",
                    type = CustomTextType.titleSmall,
                    fontSize = 14.sp,
                    color = CustomColor.black
                )
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int) {
    val steps = listOf(
        "기본 정보",
        "친구 초대",
        "확인"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, title ->
            val stepNumber = index + 1
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (currentStep >= stepNumber) CustomColor.black else CustomColor.gray50,
                    contentColor = if (currentStep >= stepNumber) Color.White else CustomColor.black,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CustomText(
                            text = stepNumber.toString(),
                            type = CustomTextType.bodyMedium,
                            color = if (currentStep >= stepNumber) Color.White else CustomColor.black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                CustomText(
                    text = title,
                    type = CustomTextType.bodySmall,
                    color = if (currentStep == stepNumber) CustomColor.black else CustomColor.gray200
                )
            }

            if (index < steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .weight(0.2f)
                        .height(1.dp)
                        .background(if (currentStep > stepNumber) CustomColor.black else CustomColor.gray50)
                )
            }
        }
    }
}

@Composable
private fun StepOneSection(
    moimName: String,
    description: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = moimName,
            onValueChange = onNameChange,
            label = { CustomText(text = "모임 제목", type = CustomTextType.bodyMedium) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { CustomText(text = "모임 설명", type = CustomTextType.bodyMedium) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
    }
}

@Composable
private fun StepTwoSection(
    friends: List<FriendProfile>,
    selectedEmails: Set<String>,
    onToggleEmail: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        CustomText(
            text = "초대할 친구를 선택하세요",
            type = CustomTextType.bodyMedium,
            color = CustomColor.gray300
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (friends.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CustomColor.gray30
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CustomText(
                        text = "초대할 친구가 없습니다.",
                        type = CustomTextType.bodyMedium,
                        color = CustomColor.gray200
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(friends, key = { it.email }) { friend ->
                    val selected = selectedEmails.contains(friend.email)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleEmail(friend.email) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) CustomColor.gray30 else Color.White,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selected) CustomColor.gray300 else CustomColor.gray100
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                CustomText(
                                    text = friend.username,
                                    type = CustomTextType.bodyMedium,
                                    color = CustomColor.black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                CustomText(
                                    text = friend.email,
                                    type = CustomTextType.bodySmall,
                                    color = CustomColor.gray200
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            if (selected) {
                                CustomText(
                                    text = "선택됨",
                                    type = CustomTextType.bodySmall,
                                    color = CustomColor.gray300,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepThreeSection(
    moimName: String,
    description: String,
    selectedFriends: List<FriendProfile>
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CustomText(
            text = "입력한 내용을 확인하세요",
            type = CustomTextType.bodyMedium,
            color = CustomColor.gray300
        )

        SummaryCard(title = "모임 제목", value = moimName)
        SummaryCard(title = "모임 설명", value = description)

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CustomColor.gray100),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                CustomText(
                    text = "초대 친구",
                    type = CustomTextType.titleMedium,
                    color = CustomColor.black
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (selectedFriends.isEmpty()) {
                    CustomText(
                        text = "선택된 친구가 없습니다.",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.gray200
                    )
                } else {
                    selectedFriends.forEachIndexed { index, friend ->
                        CustomText(
                            text = "${index + 1}. ${friend.username} (${friend.email})",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.black
                        )
                        if (index != selectedFriends.lastIndex) {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(title: String, value: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CustomColor.gray100),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CustomText(
                text = title,
                type = CustomTextType.bodySmall,
                color = CustomColor.gray200
            )
            CustomText(
                text = value,
                type = CustomTextType.bodyMedium,
                color = CustomColor.black
            )
        }
    }
}

@Composable
private fun NavigationButtons(
    currentStep: Int,
    isLoading: Boolean,
    canGoNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (currentStep > 1) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.weight(1f)
            ) {
                CustomText(text = "이전", type = CustomTextType.bodyMedium)
            }
        }

        Button(
            onClick = onNext,
            enabled = canGoNext && !isLoading,
            modifier = Modifier.weight(1f)
        ) {
            if (isLoading && currentStep == 3) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(18.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            CustomText(
                text = if (currentStep < 3) "다음" else "모임 만들기",
                type = CustomTextType.bodyMedium,
                color = Color.White
            )
        }
    }
}

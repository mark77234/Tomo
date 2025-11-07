package com.markoala.tomoandroid.ui.main.home.meeting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.main.home.meeting.components.MeetingHeader
import com.markoala.tomoandroid.ui.main.home.meeting.components.NavigationBottomButtons
import com.markoala.tomoandroid.ui.main.home.meeting.components.StepIndicator
import com.markoala.tomoandroid.ui.main.home.meeting.steps.StepOneSection
import com.markoala.tomoandroid.ui.main.home.meeting.steps.StepThreeSection
import com.markoala.tomoandroid.ui.main.home.meeting.steps.StepTwoSection
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun CreateMeetingScreen(
    paddingValues: PaddingValues,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit = {}
) {
    val viewModel: CreateMeetingViewModel = viewModel()
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val friends by viewModel.friends.collectAsState()
    val selectedEmails by viewModel.selectedEmails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var currentStep by rememberSaveable { mutableStateOf(1) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchFriends()
                viewModel.resetAllData()
                currentStep = 1
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess == true) {
            onSuccess()
            viewModel.consumeSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.background)
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        MeetingHeader(onBackClick = onBackClick)
        Spacer(modifier = Modifier.height(16.dp))
        StepIndicator(currentStep = currentStep)
        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            modifier = Modifier.weight(1f, fill = true),
            color = CustomColor.background
        ) {
            when (currentStep) {
                1 -> StepOneSection(
                    title = title,
                    description = description,
                    onNameChange = {
                        viewModel.title.value = it
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
                    onToggleEmail = {
                        viewModel.toggleEmail(it)
                        viewModel.clearError()
                    }
                )

                3 -> StepThreeSection(
                    title = title,
                    description = description,
                    selectedFriends = friends.filter { selectedEmails.contains(it.email) }
                )
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            CustomText(
                text = it,
                type = CustomTextType.bodySmall,
                color = CustomColor.danger
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        NavigationBottomButtons(
            currentStep = currentStep,
            isLoading = isLoading,
            canGoNext = when (currentStep) {
                1 -> title.isNotBlank() && description.isNotBlank()
                2 -> selectedEmails.isNotEmpty()
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
        Spacer(modifier = Modifier.height(16.dp))
    }
}

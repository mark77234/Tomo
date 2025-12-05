package com.markoala.tomoandroid.ui.main.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.components.DangerDialog
import com.markoala.tomoandroid.ui.main.settings.components.SettingsToggle
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.utils.LocationPermissionHelper
import com.markoala.tomoandroid.utils.NotificationPermissionHelper
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit = {},
) {
    SettingsContent(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.white),
        contentPadding = paddingValues,
        onSignOut = onSignOut,
        onDeleteAccount = onDeleteAccount
    )
}

@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit = {},
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    val toastManager = LocalToastManager.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var notificationsEnabled by remember { mutableStateOf(false) } // 알림 활성화 상태
    var locationPermissionGranted by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var pendingEnableViaSettings by remember { mutableStateOf(false) }
    var requestPermissionAfterSettings by remember { mutableStateOf(false) }
    var pendingLocationSettings by remember { mutableStateOf(false) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult( // 알림 권한 요청 런처
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        NotificationPermissionHelper.markPermissionRequested(context)
        notificationsEnabled = NotificationPermissionHelper.areNotificationsEnabled(context)
        if (!isGranted) { // isGranted가 false면 설정 앱으로 보내고, 나중에 돌아오면 다시 체크하도록 플래그 저장
            toastManager.showInfo("시스템 설정에서 알림설정을 허용할 수 있어요.")
            pendingEnableViaSettings = true
            requestPermissionAfterSettings = true
            NotificationPermissionHelper.openNotificationSettings(context)
        } else {
            toastManager.showInfo("푸시 알림이 켜졌어요.")
            pendingEnableViaSettings = false
            requestPermissionAfterSettings = false
        }
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        locationPermissionGranted = granted
        if (granted) {
            toastManager.showInfo("위치 권한이 켜졌어요.")
            pendingLocationSettings = false
        } else {
            toastManager.showInfo("설정에서 위치 권한을 허용할 수 있어요.")
            pendingLocationSettings = true
            LocationPermissionHelper.openAppLocationSettings(context)
        }
    }

    LaunchedEffect(Unit) { // areNotificationsEnabled() 함수로 현재 알림이 가능한 상태인지 확인
        notificationsEnabled = NotificationPermissionHelper.areNotificationsEnabled(context)
        locationPermissionGranted = LocationPermissionHelper.isLocationPermissionGranted(context)
    }

    DisposableEffect(lifecycleOwner) { // 라이프사이클 옵저버로 포그라운드 복귀 시 알림 권한 상태 재확인
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                notificationsEnabled = NotificationPermissionHelper.areNotificationsEnabled(context) // 알림이 OS에서 설정되어있는지 확인
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && requestPermissionAfterSettings) {
                    requestPermissionAfterSettings = false
                    if (!NotificationPermissionHelper.isPermissionGranted(context)) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        return@LifecycleEventObserver
                    }
                }
                if (pendingEnableViaSettings && notificationsEnabled) {
                    toastManager.showInfo("푸시 알림이 켜졌어요.")
                    pendingEnableViaSettings = false
                }
                val updatedLocationPermission = LocationPermissionHelper.isLocationPermissionGranted(context)
                if (pendingLocationSettings) {
                    val message = if (updatedLocationPermission) {
                        "위치 권한이 켜졌어요."
                    } else {
                        "위치 권한이 꺼져 있어요."
                    }
                    toastManager.showInfo(message)
                    pendingLocationSettings = false
                }
                locationPermissionGranted = updatedLocationPermission
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (showDeleteDialog) {
        DangerDialog(
            title = "토모와의 이별",
            message = "정말로 계정을 삭제하시겠습니까?\n토모는 언제든지 기다리고 있을게요.\n우린 토모니까.",
            confirmText = "삭제",
            dismissText = "취소",
            isLoading = isDeleting,
            onConfirm = {
                isDeleting = true
                coroutineScope.launch {
                    val (success, error) = AuthManager.deleteAccount(context)
                    isDeleting = false
                    showDeleteDialog = false
                    if (success) {
                        toastManager.showSuccess("계정이 삭제되었습니다.")
                        onDeleteAccount()
                    } else {
                        toastManager.showError(error ?: "계정 삭제에 실패했습니다.")
                    }
                }
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CustomColor.white)
            .padding(contentPadding)
            .padding( vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = CustomColor.gray200,
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.white
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsToggle(
                    title = "푸시 알림",
                    description = "모임과 친구 소식을 알림으로 받아요",
                    checked = notificationsEnabled,
                    onCheckedChange = { enable ->
                        notificationsEnabled = enable
                        if (enable) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val permissionGranted = NotificationPermissionHelper.isPermissionGranted(context) // 앱 내 권한 설정 여부
                                val notificationsAllowed = NotificationPermissionHelper.areNotificationsEnabled(context) // 시스템 설정에서 알림 허용 여부
                                when {
                                    !permissionGranted -> {
                                        NotificationPermissionHelper.markPermissionRequested(context)
                                        pendingEnableViaSettings = true
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    !notificationsAllowed -> {
                                        pendingEnableViaSettings = true
                                        requestPermissionAfterSettings = true
                                        NotificationPermissionHelper.openNotificationSettings(context)
                                        toastManager.showInfo("설정에서 알림을 켜주세요.")
                                    }
                                    else -> toastManager.showInfo("푸시 알림이 켜졌어요.")
                                }
                            } else {
                                if (!NotificationPermissionHelper.areNotificationsEnabled(context)) {
                                    pendingEnableViaSettings = true
                                    requestPermissionAfterSettings = false
                                    NotificationPermissionHelper.openNotificationSettings(context)
                                    toastManager.showInfo("설정에서 알림을 켜주세요.")
                                } else {
                                    toastManager.showInfo("푸시 알림이 켜졌어요.")
                                }
                            }
                        } else {
                            NotificationPermissionHelper.resetPermissionRequested(context)
                            NotificationPermissionHelper.openNotificationSettings(context)
                            toastManager.showInfo("설정에서 토모 알림을 끌 수 있어요.")
                            pendingEnableViaSettings = false
                            requestPermissionAfterSettings = false
                        }
                    },
                    icon = R.drawable.ic_notification
                )
                SettingsToggle(
                    title = "위치 접근",
                    description = "현재 위치 버튼을 사용하려면 허용이 필요해요",
                    checked = locationPermissionGranted,
                    onCheckedChange = { enable ->
                        locationPermissionGranted = enable
                        if (enable) {
                            if (LocationPermissionHelper.isLocationPermissionGranted(context)) {
                                locationPermissionGranted = true
                                toastManager.showInfo("위치 권한이 이미 켜져 있어요.")
                                pendingLocationSettings = false
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        } else {
                            pendingLocationSettings = true
                            LocationPermissionHelper.openAppLocationSettings(context)
                            toastManager.showInfo("설정에서 위치 권한을 끌 수 있어요.")
                        }
                    },
                    icon = R.drawable.ic_location
                )
                CustomText(
                    text = "알림 권한은 기기 설정에서 관리돼요.",
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )
                CustomText(
                    text = "위치 권한은 앱 설정에서 켜고 끌 수 있어요.",
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 계정 관리 섹션
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.primaryContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_setting),
                    contentDescription = null,
                    tint = CustomColor.primary,
                    modifier = Modifier.size(48.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomText(
                        text = "계정 관리",
                        type = CustomTextType.title,
                        color = CustomColor.primary
                    )
                    CustomText(
                        text = "로그아웃하거나 계정을 영구적으로 삭제할 수 있습니다",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.primaryDim
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 로그아웃 카드
        Surface(
            modifier = Modifier.fillMaxWidth().border(
                width = 1.dp,
                color = CustomColor.gray200,
                shape = RoundedCornerShape(24.dp)
            ),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.white
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = null,
                        tint = CustomColor.textSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        CustomText(
                            text = "로그아웃",
                            type = CustomTextType.body,
                            color = CustomColor.textPrimary
                        )
                        CustomText(
                            text = "현재 기기에서 로그아웃합니다",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.textSecondary
                        )
                    }
                }

                CustomButton(
                    text = "로그아웃",
                    onClick = {
                        onSignOut()

                    },
                    style = ButtonStyle.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 계정 삭제 카드
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.danger.copy(alpha = 0.05f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = CustomColor.danger,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        CustomText(
                            text = "계정 삭제",
                            type = CustomTextType.body,
                            color = CustomColor.danger
                        )
                        CustomText(
                            text = "모든 데이터가 영구적으로 삭제됩니다",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.danger.copy(alpha = 0.7f)
                        )
                    }
                }

                CustomButton(
                    text = "계정 삭제",
                    onClick = { showDeleteDialog = true },
                    style = ButtonStyle.Danger,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

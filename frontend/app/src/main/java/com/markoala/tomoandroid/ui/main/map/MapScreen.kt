package com.markoala.tomoandroid.ui.main.map

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.markoala.tomoandroid.data.api.GeocodeAddress
import com.markoala.tomoandroid.data.api.GeocodeResponse
import com.markoala.tomoandroid.data.api.NaverMapGeocodeClient
import com.markoala.tomoandroid.BuildConfig
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.CustomTextField
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapScreen(
    paddingValues: PaddingValues
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()
    val toastManager = LocalToastManager.current
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var geocodeResults by remember { mutableStateOf<List<GeocodeAddress>>(emptyList()) }
    val geocodeAvailable = BuildConfig.NAVER_MAP_CLIENT_ID.isNotBlank() &&
        BuildConfig.NAVER_MAP_CLIENT_SECRET.isNotBlank()

    val defaultPos = LatLng(37.5666102, 126.9783881)
    val cameraState = rememberCameraPositionState {
        position = CameraPosition(defaultPos, 14.0)
    }

    var hasLocationPermission by remember { mutableStateOf(checkLocationPermission(context)) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasLocationPermission = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!hasLocationPermission) {
            toastManager.showInfo("위치 권한을 허용하면 현재 위치로 이동할 수 있어요.")
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        val clientId = BuildConfig.NAVER_MAP_CLIENT_ID
        Log.d("MapScreen", "Naver Map Client ID: $clientId")
        if (clientId.isNotBlank()) {
            @Suppress("DEPRECATION")
            NaverMapSdk.getInstance(appContext).client =
                NaverMapSdk.NcpKeyClient(clientId)
        } else {
            toastManager.showInfo("네이버 지도 클라이언트 ID가 설정되지 않았어요.")
        }
        if (!geocodeAvailable) {
            toastManager.showInfo("지오코딩 키가 설정되지 않았어요. 주소 검색은 VPC 환경에서만 동작해요.")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.white)
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomText(
            text = "지도",
            type = CustomTextType.headline,
            color = CustomColor.textPrimary
        )
        CustomText(
            text = "네이버 지도로 모임 위치를 탐색할 수 있도록 준비했어요.",
            type = CustomTextType.bodySmall,
            color = CustomColor.textSecondary
        )
        CustomText(
            text = "Geocoding은 VPC 환경에서 이용 가능하며, 입력한 주소와 연관된 주소 정보를 검색해요.",
            type = CustomTextType.bodySmall,
            color = CustomColor.textSecondary
        )

        CustomTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = "검색할 주소를 입력하세요",
            supportingText = "예) 분당구 불정로 6",
            modifier = Modifier.fillMaxWidth()
        )

        CustomButton(
            text = if (isSearching) "검색 중..." else "주소 검색",
            onClick = {
                if (!geocodeAvailable) {
                    toastManager.showInfo("지오코딩 키가 설정되지 않았어요.")
                    return@CustomButton
                }
                if (searchQuery.isBlank()) {
                    toastManager.showInfo("검색할 주소를 입력해주세요.")
                    return@CustomButton
                }
                scope.launch {
                    isSearching = true
                    try {
                        val response = geocodeAddress(
                            query = searchQuery.trim(),
                            coordinate = cameraState.position.target
                        )
                        if (response.status == "OK") {
                            geocodeResults = response.addresses.orEmpty()
                            if (geocodeResults.isEmpty()) {
                                toastManager.showInfo("검색 결과가 없어요.")
                            }
                        } else {
                            val message = response.errorMessage?.ifBlank { null }
                                ?: "주소 검색에 실패했어요."
                            toastManager.showInfo(message)
                        }
                    } catch (e: Exception) {
                        Log.w("MapScreen", "Geocode request failed", e)
                        toastManager.showInfo("주소 검색 중 문제가 발생했어요.")
                    } finally {
                        isSearching = false
                    }
                }
            },
            enabled = searchQuery.isNotBlank() && !isSearching && geocodeAvailable,
            style = ButtonStyle.Secondary,
            modifier = Modifier.fillMaxWidth()
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.background
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(20.dp),
                    color = CustomColor.white
                ) {
                    NaverMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraState,
                        properties = MapProperties(),
                        uiSettings = MapUiSettings()
                    )
                }
            }
        }

        GeocodeResultSection(
            results = geocodeResults,
            isSearching = isSearching,
            onSelect = { address ->
                val target = address.toLatLng()
                if (target != null) {
                    cameraState.move(CameraUpdate.scrollTo(target))
                    cameraState.move(CameraUpdate.zoomTo(16.0))
                } else {
                    toastManager.showInfo("좌표 정보를 불러올 수 없어요.")
                }
            }
        )

        CustomButton(
            text = if (hasLocationPermission) "현재 위치로 이동" else "위치 권한 요청",
            onClick = {
                if (!hasLocationPermission) {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                    return@CustomButton
                }
                scope.launch {
                    try {
                        val latLng = fetchCurrentLatLng(context, fusedClient)
                        if (latLng != null) {
                            cameraState.move(CameraUpdate.scrollTo(latLng))
                            cameraState.move(CameraUpdate.zoomTo(16.0))
                        } else {
                            toastManager.showInfo("현재 위치를 불러올 수 없어요.")
                        }
                    } catch (se: SecurityException) {
                        hasLocationPermission = false
                        toastManager.showInfo("위치 권한을 다시 확인해주세요.")
                    }
                }
            },
            style = ButtonStyle.Primary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))
    }
}

private suspend fun geocodeAddress(
    query: String,
    coordinate: LatLng?
): GeocodeResponse = withContext(Dispatchers.IO) {
    val coordinateParam = coordinate?.let { "${it.longitude},${it.latitude}" }
    NaverMapGeocodeClient.api.geocode(
        query = query,
        coordinate = coordinateParam,
        language = "kor",
        page = 1,
        count = 10
    )
}

@Composable
private fun GeocodeResultSection(
    results: List<GeocodeAddress>,
    isSearching: Boolean,
    onSelect: (GeocodeAddress) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 0.dp, max = 220.dp),
        shape = RoundedCornerShape(16.dp),
        color = CustomColor.background
    ) {
        when {
            isSearching -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CustomText(
                        text = "주소를 검색 중이에요...",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.textSecondary
                    )
                }
            }

            results.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    CustomText(
                        text = "검색 결과가 여기에 표시돼요.",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.textSecondary
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    results.forEach { address ->
                        GeocodeResultItem(
                            address = address,
                            onSelect = { onSelect(address) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GeocodeResultItem(
    address: GeocodeAddress,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(12.dp),
        color = CustomColor.white
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val title = address.roadAddress?.takeIf { it.isNotBlank() }
                ?: address.jibunAddress?.takeIf { it.isNotBlank() }
                ?: "주소 정보를 불러올 수 없어요."
            CustomText(
                text = title,
                type = CustomTextType.body,
                color = CustomColor.textPrimary
            )
            address.jibunAddress
                ?.takeIf { it.isNotBlank() && it != title }
                ?.let {
                    CustomText(
                        text = it,
                        type = CustomTextType.bodySmall,
                        color = CustomColor.textSecondary
                    )
                }
            address.distance?.let {
                CustomText(
                    text = "거리: ${"%.0f".format(it)}m",
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )
            }
        }
    }
}

private fun GeocodeAddress.toLatLng(): LatLng? {
    val lat = y?.toDoubleOrNull()
    val lng = x?.toDoubleOrNull()
    return if (lat != null && lng != null) {
        LatLng(lat, lng)
    } else {
        null
    }
}

private fun checkLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    val coarse = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    return fine || coarse
}

private suspend fun fetchCurrentLatLng(
    context: Context,
    fusedClient: com.google.android.gms.location.FusedLocationProviderClient
): LatLng? {
    if (!checkLocationPermission(context)) return null
    val cancellationTokenSource = CancellationTokenSource()
    val current = fusedClient.getCurrentLocation(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        cancellationTokenSource.token
    ).await()
    val location = current ?: fusedClient.lastLocation.await()
    return location?.let { LatLng(it.latitude, it.longitude) }
}

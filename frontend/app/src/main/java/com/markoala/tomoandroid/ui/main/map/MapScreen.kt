package com.markoala.tomoandroid.ui.main.map

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapAuthException
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelTextBuilder
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.data.api.GeocodeAddress
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.utils.LocationPermissionHelper
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun MapScreen(
    paddingValues: PaddingValues,
    selectedAddress: GeocodeAddress?,
    selectedQuery: String?,
    onSearchClick: () -> Unit,
    onCreatePromiseWithLocation: (GeocodeAddress, String?) -> Unit = { _, _ -> },
    interactive: Boolean = true,
    isPromise: Boolean = true,
    showSearchOverlay: Boolean = true
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()
    val toastManager = LocalToastManager.current

    val defaultPos = LatLng.from(37.5666102, 126.9783881)
    val selectedAddressState = rememberUpdatedState(selectedAddress)

    var hasLocationPermission by remember { mutableStateOf(LocationPermissionHelper.isLocationPermissionGranted(context)) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasLocationPermission = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!hasLocationPermission) {
            toastManager.showInfo("위치 권한을 허용하면 현재 위치로 이동할 수 있어요.")
        }
    }

    LaunchedEffect(isPromise) {
        if (isPromise && !hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val mapView = remember { MapView(appContext) }
    var kakaoMap by remember { mutableStateOf<KakaoMap?>(null) }
    var marker by remember { mutableStateOf<Label?>(null) }
    var currentLocationMarker by remember { mutableStateOf<Label?>(null) }

    DisposableEffect(lifecycleOwner, mapView) {
        val mapLifeCycle = object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                marker?.remove()
                marker = null
                currentLocationMarker?.remove()
                currentLocationMarker = null
            }

            override fun onMapError(error: Exception) {
                val message = if (error is MapAuthException) {
                    "카카오맵 인증에 실패했어요. 키 설정을 확인해주세요."
                } else {
                    "지도 초기화 중 오류가 발생했어요."
                }
                toastManager.showInfo(message)
                Log.e("MapScreen", "Kakao map error", error)
            }
        }

        val readyCallback = object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                kakaoMap = map
                val target = selectedAddressState.value?.toLatLng() ?: defaultPos
                map.moveCamera(CameraUpdateFactory.newCenterPosition(target, 14))
                selectedAddressState.value?.let { address ->
                    marker = placeMarker(
                        map = map,
                        currentLabel = marker,
                        position = target,
                        title = address.displayTitle(),
                        labelId = "selected_marker",
                        iconRes = R.drawable.ic_marker_primary,
                        context = context
                    )
                }
                currentLocation?.let { location ->
                    currentLocationMarker = placeMarker(
                        map = map,
                        currentLabel = currentLocationMarker,
                        position = location,
                        title = "현재 위치",
                        labelId = "current_marker",
                        iconRes = R.drawable.ic_marker_current,
                        context = context
                    )
                }
            }
        }

        mapView.start(mapLifeCycle, readyCallback)

        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    mapView.resume()
                    hasLocationPermission = LocationPermissionHelper.isLocationPermissionGranted(context)
                }
                Lifecycle.Event.ON_PAUSE -> mapView.pause()
                Lifecycle.Event.ON_DESTROY -> mapView.finish()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)

        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            mapView.resume()
        }

        onDispose {
            lifecycle.removeObserver(observer)
            mapView.finish()
        }
    }

    LaunchedEffect(kakaoMap, selectedAddress?.x, selectedAddress?.y) {
        val map = kakaoMap ?: return@LaunchedEffect
        val target = selectedAddress?.toLatLng()
        if (target != null) {
            map.moveCamera(CameraUpdateFactory.newCenterPosition(target, 16))
            marker = placeMarker(
                map = map,
                currentLabel = marker,
                position = target,
                title = selectedAddress.displayTitle(),
                labelId = "selected_marker",
                iconRes = R.drawable.ic_marker_primary,
                context = context
            )
        } else {
            marker?.remove()
            marker = null
        }
    }

    LaunchedEffect(kakaoMap, currentLocation) {
        val map = kakaoMap ?: return@LaunchedEffect
        val location = currentLocation ?: run {
            currentLocationMarker?.remove()
            currentLocationMarker = null
            return@LaunchedEffect
        }
        currentLocationMarker = placeMarker(
            map = map,
            currentLabel = currentLocationMarker,
            position = location,
            title = "현재 위치",
            labelId = "current_marker",
            iconRes = R.drawable.ic_marker_current,
            context = context
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.white)
            .padding(paddingValues)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter { !interactive },
            factory = {
                mapView.apply {
                    isEnabled = interactive
                    isClickable = interactive
                    isLongClickable = interactive
                    setOnTouchListener(
                        if (interactive) { v, event ->
                            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                                v.parent?.requestDisallowInterceptTouchEvent(true)
                            }
                            false
                        } else { _, _ ->
                            true
                        }
                    )
                }
            }
        )

        if (showSearchOverlay) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable { onSearchClick() },
                shape = RoundedCornerShape(14.dp),
                color = CustomColor.white,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CustomText(
                        text = "모임을 가질 장소를 검색해보세요.",
                        type = CustomTextType.bodySmall,
                        color = CustomColor.textSecondary
                    )
                    CustomText(
                        text = selectedQuery?.takeIf { it.isNotBlank() }
                            ?: "장소를 검색하려면 눌러주세요",
                        type = CustomTextType.body,
                        color = CustomColor.textPrimary
                    )
                }
            }
        }

        selectedAddress?.let { address ->
            val bottomPadding = if (isPromise) 82.dp else 16.dp
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = bottomPadding),
                shape = RoundedCornerShape(16.dp),
                color = CustomColor.white,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val title = address.displayTitle()
                    CustomText(
                        text = title,
                        type = CustomTextType.body,
                        color = CustomColor.textPrimary
                    )
                    address.roadAddress
                        ?.takeIf { it.isNotBlank() && it != title }
                        ?.let {
                            CustomText(
                                text = it,
                                type = CustomTextType.bodySmall,
                                color = CustomColor.textSecondary
                            )
                        }
                    address.jibunAddress
                        ?.takeIf { it.isNotBlank() && it != title }
                        ?.let {
                            CustomText(
                                text = it,
                                type = CustomTextType.bodySmall,
                                color = CustomColor.textSecondary
                            )
                        }
                    address.englishAddress
                        ?.takeIf { it.isNotBlank() }
                        ?.let {
                            CustomText(
                                text = it,
                                type = CustomTextType.bodySmall,
                                color = CustomColor.textSecondary
                            )
                        }
                    if (isPromise) {
                        CustomButton(
                            text = "이 장소로 약속 잡기",
                            onClick = { onCreatePromiseWithLocation(address, selectedQuery) },
                            style = ButtonStyle.Primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                }
            }
        }
        if (isPromise) {

            // hasLocationPermission == true → 아이콘 버튼 표시
            if (hasLocationPermission) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 24.dp, end = 16.dp)
                        .size(45.dp)
                        .background(
                            color = CustomColor.primary,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .clickable {
                            scope.launch {
                                try {
                                    val latLng = fetchCurrentLatLng(context, fusedClient)
                                    if (latLng != null) {
                                        kakaoMap?.moveCamera(
                                            CameraUpdateFactory.newCenterPosition(latLng, 16)
                                        ) ?: toastManager.showInfo("지도를 준비하는 중이에요.")
                                        currentLocation = latLng
                                    } else {
                                        toastManager.showInfo("현재 위치를 불러올 수 없어요.")
                                    }
                                } catch (se: SecurityException) {
                                    hasLocationPermission = false
                                    toastManager.showInfo("위치 권한을 다시 확인해주세요.")
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_current_location),
                        contentDescription = "현재 위치",
                        modifier = Modifier.size(24.dp),
                        tint = CustomColor.white
                    )
                }
            }
            // hasLocationPermission == false → 기존 버튼 유지
            else {
                CustomButton(
                    text = "위치 권한 요청",
                    onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    style = ButtonStyle.Primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }

    }
}

private fun placeMarker(
    map: KakaoMap,
    currentLabel: Label?,
    position: LatLng,
    title: String,
    labelId: String,
    iconRes: Int,
    context: Context
): Label? {
    currentLabel?.remove()
    val layer: LabelLayer = map.labelManager?.layer ?: return null
    val iconBitmap = bitmapFromVector(context, iconRes) ?: return null
    val style = LabelStyle.from(iconBitmap)
        .setAnchorPoint(0.5f, 1f)
    val options = LabelOptions.from(labelId, position)
        .setStyles(style)
        .setTexts(LabelTextBuilder().setTexts(title.take(30)))
    return layer.addLabel(options)
}

private fun bitmapFromVector(context: Context, drawableResId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(context, drawableResId) ?: return null
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth.coerceAtLeast(1),
        drawable.intrinsicHeight.coerceAtLeast(1),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

private fun GeocodeAddress.toLatLng(): LatLng? {
    val lat = y?.toDoubleOrNull()
    val lng = x?.toDoubleOrNull()
    return if (lat != null && lng != null) {
        LatLng.from(lat, lng)
    } else {
        null
    }
}

private fun GeocodeAddress.displayTitle(): String {
    return name?.takeIf { it.isNotBlank() }
        ?: roadAddress?.takeIf { it.isNotBlank() }
        ?: jibunAddress?.takeIf { it.isNotBlank() }
        ?: englishAddress?.takeIf { it.isNotBlank() }
        ?: "선택한 장소 정보를 불러올 수 없어요."
}

private suspend fun fetchCurrentLatLng(
    context: Context,
    fusedClient: com.google.android.gms.location.FusedLocationProviderClient
): LatLng? {
    if (!LocationPermissionHelper.isLocationPermissionGranted(context)) return null
    return try {
        val cancellationTokenSource = CancellationTokenSource()
        val current = fusedClient.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            cancellationTokenSource.token
        ).await()
        val location = current ?: fusedClient.lastLocation.await()
        location?.let { LatLng.from(it.latitude, it.longitude) }
    } catch (se: SecurityException) {
        null
    }
}

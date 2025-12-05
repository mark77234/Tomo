package com.markoala.tomoandroid.ui.main.map.map_search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.BuildConfig
import com.markoala.tomoandroid.data.api.GeocodeAddress
import com.markoala.tomoandroid.data.api.KakaoLocalClient
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomBack
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.CustomTextField
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.theme.CustomColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MapSearchScreen(
    paddingValues: PaddingValues,
    initialQuery: String?,
    onBackClick: () -> Unit,
    onSelect: (query: String, address: GeocodeAddress) -> Unit
) {
    val toastManager = LocalToastManager.current
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf(initialQuery.orEmpty()) }
    var isSearching by remember { mutableStateOf(false) }
    var results by remember { mutableStateOf<List<GeocodeAddress>>(emptyList()) }
    val geocodeAvailable = BuildConfig.KAKAO_REST_API_KEY.isNotBlank()

    LaunchedEffect(initialQuery) {
        if (!initialQuery.isNullOrBlank() && geocodeAvailable) {
            isSearching = true
            try {
                results = searchPlaces(initialQuery.trim())
            } catch (_: Exception) {
                toastManager.showInfo("주소 검색 중 문제가 발생했어요.")
            } finally {
                isSearching = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomBack(onClick = onBackClick)

        CustomText(
            text = "모임 장소를 검색해보세요.",
            type = CustomTextType.headline,
            color = CustomColor.textPrimary
        )
        CustomText(
            text = "검색한 주소와 가까운 순으로 장소가 표시돼요. 검색은 VPC 환경에서 동작해요.",
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
                    toastManager.showInfo("카카오 로컬 API 키가 설정되지 않았어요.")
                    return@CustomButton
                }
                if (searchQuery.isBlank()) {
                    toastManager.showInfo("검색할 주소를 입력해주세요.")
                    return@CustomButton
                }
                scope.launch {
                    isSearching = true
                    try {
                        results = searchPlaces(searchQuery.trim())
                        if (results.isEmpty()) {
                            toastManager.showInfo("검색 결과가 없어요.")
                        }
                    } catch (e: Exception) {
                        toastManager.showInfo("주소 검색 중 문제가 발생했어요.")
                    } finally {
                        isSearching = false
                    }
                }
            },
            enabled = searchQuery.isNotBlank() && !isSearching && geocodeAvailable,
            style = ButtonStyle.Primary,
            modifier = Modifier.fillMaxWidth()
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = CustomColor.background
        ) {
            when {
                isSearching -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        CustomText(
                            text = "주소를 검색 중이에요...",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.textSecondary
                        )
                    }
                }

                results.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        CustomText(
                            text = "검색 결과가 여기에 표시돼요.",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.textSecondary
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(results) { address ->
                            GeocodeResultItem(
                                address = address,
                                onSelect = { onSelect(searchQuery.trim(), address) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(4.dp)) }
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
            .padding(horizontal = 4.dp),
        onClick = onSelect,
        shape = RoundedCornerShape(12.dp),
        color = CustomColor.white,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val title = address.name?.takeIf { it.isNotBlank() }
                ?: address.roadAddress?.takeIf { it.isNotBlank() }
                ?: address.jibunAddress?.takeIf { it.isNotBlank() }
                ?: "주소 정보를 불러올 수 없어요."
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

private suspend fun searchPlaces(
    query: String
): List<GeocodeAddress> = withContext(Dispatchers.IO) {
    val collected = mutableListOf<GeocodeAddress>()

    runCatching {
        KakaoLocalClient.api.searchKeyword(
            query = query,
            size = 15
        )
    }.onSuccess { response ->
        collected += response.documents.orEmpty().map { it.toGeocodeAddress() }
    }

    runCatching {
        KakaoLocalClient.api.searchAddress(
            query = query,
            size = 15
        )
    }.onSuccess { response ->
        collected += response.documents.orEmpty().map { it.toGeocodeAddress() }
    }

    collected.distinctBy { "${it.x}|${it.y}|${it.roadAddress}|${it.jibunAddress}|${it.name}" }
}

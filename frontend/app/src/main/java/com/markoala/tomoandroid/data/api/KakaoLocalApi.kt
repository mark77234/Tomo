package com.markoala.tomoandroid.data.api

import com.google.gson.annotations.SerializedName
import com.markoala.tomoandroid.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoLocalApi {
    @GET("v2/local/search/keyword.json")
    suspend fun searchKeyword(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 15
    ): KakaoKeywordResponse

    @GET("v2/local/search/address.json")
    suspend fun searchAddress(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 15
    ): KakaoAddressResponse
}

object KakaoLocalClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}")
            .addHeader("Accept", "application/json")
            .build()
        chain.proceed(request)
    }

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://dapi.kakao.com/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: KakaoLocalApi = retrofit.create(KakaoLocalApi::class.java)
}

data class KakaoKeywordResponse(
    val meta: KakaoMeta?,
    val documents: List<KakaoKeywordDocument>?
)

data class KakaoAddressResponse(
    val meta: KakaoMeta?,
    val documents: List<KakaoAddressDocument>?
)

data class KakaoMeta(
    @SerializedName("total_count") val totalCount: Int?,
    @SerializedName("pageable_count") val pageableCount: Int?,
    @SerializedName("is_end") val isEnd: Boolean?
)

data class KakaoKeywordDocument(
    val id: String?,
    @SerializedName("place_name") val placeName: String?,
    @SerializedName("category_name") val categoryName: String?,
    @SerializedName("category_group_code") val categoryGroupCode: String?,
    @SerializedName("category_group_name") val categoryGroupName: String?,
    val phone: String?,
    @SerializedName("address_name") val addressName: String?,
    @SerializedName("road_address_name") val roadAddressName: String?,
    val x: String?,
    val y: String?,
    @SerializedName("place_url") val placeUrl: String?,
    val distance: String?
) {
    fun toGeocodeAddress(): GeocodeAddress = GeocodeAddress(
        name = placeName,
        roadAddress = roadAddressName?.takeIf { it.isNotBlank() } ?: addressName,
        jibunAddress = addressName ?: roadAddressName,
        englishAddress = null,
        addressElements = null,
        x = x,
        y = y,
        distance = distance?.toDoubleOrNull()
    )
}

data class KakaoAddressDocument(
    @SerializedName("address_name") val addressName: String?,
    @SerializedName("address") val address: KakaoAddressInfo?,
    @SerializedName("road_address") val roadAddress: KakaoRoadAddress?
) {
    fun toGeocodeAddress(): GeocodeAddress {
        val road = roadAddress?.addressName?.takeIf { it.isNotBlank() }
            ?: roadAddress?.buildingName?.takeIf { it.isNotBlank() }
        val jibun = address?.addressName?.takeIf { it.isNotBlank() }
            ?: addressName
            ?: road
        val lat = roadAddress?.y ?: address?.y
        val lng = roadAddress?.x ?: address?.x

        return GeocodeAddress(
            name = road ?: jibun ?: addressName,
            roadAddress = road ?: address?.addressName,
            jibunAddress = jibun,
            englishAddress = address?.englishAddress,
            addressElements = null,
            x = lng,
            y = lat,
            distance = null
        )
    }
}

data class KakaoRoadAddress(
    @SerializedName("address_name") val addressName: String?,
    @SerializedName("building_name") val buildingName: String?,
    @SerializedName("zone_no") val zoneNo: String?,
    val x: String?,
    val y: String?
)

data class KakaoAddressInfo(
    @SerializedName("address_name") val addressName: String?,
    @SerializedName("region_1depth_name") val region1DepthName: String?,
    @SerializedName("region_2depth_name") val region2DepthName: String?,
    @SerializedName("region_3depth_name") val region3DepthName: String?,
    @SerializedName("mountain_yn") val mountainYn: String?,
    @SerializedName("main_address_no") val mainAddressNo: String?,
    @SerializedName("sub_address_no") val subAddressNo: String?,
    @SerializedName("zip_code") val zipCode: String?,
    @SerializedName("english_address") val englishAddress: String?,
    val x: String?,
    val y: String?
)

data class GeocodeAddress(
    val name: String? = null,
    val roadAddress: String? = null,
    val jibunAddress: String? = null,
    val englishAddress: String? = null,
    val addressElements: List<GeocodeAddressElement>? = null,
    val x: String? = null,
    val y: String? = null,
    val distance: Double? = null
)

data class GeocodeAddressElement(
    @SerializedName("types") val types: List<String>?,
    val longName: String?,
    val shortName: String?,
    val code: String?
)

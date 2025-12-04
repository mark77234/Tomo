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

interface NaverMapGeocodeApi {
    @GET("map-geocode/v2/geocode")
    suspend fun geocode(
        @Query("query") query: String,
        @Query("coordinate") coordinate: String? = null,
        @Query("filter") filter: String? = null,
        @Query("language") language: String? = "kor",
        @Query("page") page: Int? = 1,
        @Query("count") count: Int? = 10
    ): GeocodeResponse
}

object NaverMapGeocodeClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("x-ncp-apigw-api-key-id", BuildConfig.NAVER_MAP_CLIENT_ID)
            .addHeader("x-ncp-apigw-api-key", BuildConfig.NAVER_MAP_CLIENT_SECRET)
            .addHeader("Accept", "application/json")
            .build()
        chain.proceed(request)
    }

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://maps.apigw.ntruss.com/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: NaverMapGeocodeApi = retrofit.create(NaverMapGeocodeApi::class.java)
}

data class GeocodeResponse(
    val status: String?,
    val meta: GeocodeMeta?,
    val addresses: List<GeocodeAddress>?,
    @SerializedName("errorMessage") val errorMessage: String?
)

data class GeocodeMeta(
    val totalCount: Int?,
    val page: Int?,
    val count: Int?
)

data class GeocodeAddress(
    val roadAddress: String?,
    val jibunAddress: String?,
    val englishAddress: String?,
    val addressElements: List<GeocodeAddressElement>?,
    val x: String?,
    val y: String?,
    val distance: Double?
)

data class GeocodeAddressElement(
    @SerializedName("types") val types: List<String>?,
    val longName: String?,
    val shortName: String?,
    val code: String?
)

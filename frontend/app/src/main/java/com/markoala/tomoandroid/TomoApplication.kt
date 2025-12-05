package com.markoala.tomoandroid

import android.app.Application
import android.util.Log
import com.kakao.vectormap.KakaoMapSdk

class TomoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val appKey = BuildConfig.KAKAO_MAP_NATIVE_APP_KEY
        if (appKey.isNotBlank()) {
            KakaoMapSdk.init(this, appKey)
        } else {
            Log.w("TomoApplication", "Kakao map native app key is missing; maps will not authenticate.")
        }
    }
}

import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

// local.properties 파일 읽기
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.markoala.tomoandroid"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.markoala.tomoandroid"
        minSdk = 33
        targetSdk = 36
        versionCode = 7
        versionName = "1.0.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig에 default_web_client_id 추가
        buildConfigField(
            "String",
            "DEFAULT_WEB_CLIENT_ID",
            "\"${localProperties.getProperty("default_web_client_id", "")}\""
        )

        // BuildConfig에 BASE_URL 추가
        buildConfigField(
            "String",
            "BASE_URL",
            "\"${localProperties.getProperty("base_url", "https://markoala.shop/")}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true // BuildConfig 활성화
    }
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BoM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // ✅ Firebase BoM (버전 카탈로그 사용)
    implementation(platform(libs.firebase.bom))

    // Firebase Authentication (버전 명시 X → BoM이 관리)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)

    // Android Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)

    // Google Identity Services
    implementation(libs.googleid)
    implementation(libs.navigation.compose)

    // Retrofit 의존성 추가
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.gson)

    // Coil for image loading
    implementation(libs.coil.compose)

    // Security Crypto for encrypted shared preferences
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    // OkHttp for HTTP client
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.firebase.firestore)
}
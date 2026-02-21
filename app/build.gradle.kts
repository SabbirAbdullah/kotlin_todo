// ============================================================
//  app/build.gradle.kts
//  Updated for AGP 9.0 + Kotlin 2.2.10 + Compose BOM 2026.01.01
//  February 2026
// ============================================================

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // ⭐ Kotlin 2.0+ Compose compiler plugin.
    // This fully REPLACES the old composeOptions { kotlinCompilerExtensionVersion }
    // block. The plugin auto-syncs the compiler version with your Kotlin version.
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace  = "com.example.taskmanager"
    compileSdk = 36

    defaultConfig {
        applicationId   = "com.example.taskmanager"
        minSdk          = 26
        targetSdk       = 36
        versionCode     = 1
        versionName     = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    // ✅ NO composeOptions block needed anymore with Kotlin 2.0+ compose plugin!
    // kotlinCompilerExtensionVersion is handled automatically.

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.navigation.compose)
    // ── Compose BOM ───────────────────────────────────────────────────────────
    // Single import pins ALL androidx.compose.* to compatible versions
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // ── Core Android ──────────────────────────────────────────────────────────
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ── Compose UI ────────────────────────────────────────────────────────────
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.animation)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ── Navigation ────────────────────────────────────────────────────────────
    implementation(libs.androidx.navigation.compose)

    // ── Hilt DI ───────────────────────────────────────────────────────────────
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)                  // KSP — NOT kapt
    implementation(libs.androidx.hilt.navigation.compose)

    // ── Retrofit ──────────────────────────────────────────────────────────────
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // ── OkHttp ────────────────────────────────────────────────────────────────
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // ── Gson ──────────────────────────────────────────────────────────────────
    implementation(libs.gson)

    // ── Room ──────────────────────────────────────────────────────────────────
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)                 // KSP — NOT kapt

    // ── Lifecycle & ViewModel ─────────────────────────────────────────────────
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // ── Coroutines ────────────────────────────────────────────────────────────
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // ── DataStore (Token Storage) ─────────────────────────────────────────────
    implementation(libs.androidx.datastore.preferences)

    // ── Splash Screen ─────────────────────────────────────────────────────────
    implementation(libs.androidx.core.splashscreen)

    // ── Tests ─────────────────────────────────────────────────────────────────
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
// ============================================================
//  build.gradle.kts  (Project / Root level)
//  Updated for AGP 9.0 + Kotlin 2.2.10  —  February 2026
// ============================================================

plugins {
    alias(libs.plugins.android.application)  apply false
    alias(libs.plugins.android.library)      apply false
    alias(libs.plugins.kotlin.android)       apply false

    // ⭐ Compose Compiler plugin — replaces composeOptions block in AGP
    alias(libs.plugins.kotlin.compose)       apply false

    alias(libs.plugins.ksp)                  apply false
    alias(libs.plugins.hilt.android)         apply false
}
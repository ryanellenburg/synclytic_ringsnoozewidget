plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.synclytic.ringsnoozewidget"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.synclytic.ringsnoozewidget"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx) // Kotlin extensions for core Android framework
    implementation(libs.androidx.lifecycle.runtime.ktx)  // Lifecycle-aware components for Kotlin
    implementation(libs.androidx.activity.compose) // Compose support for Activities
    implementation(platform(libs.androidx.compose.bom)) // Compose Bill of Materials (BOM) for dependency management
    implementation(libs.androidx.ui) // Jetpack Compose UI toolkit
    implementation(libs.androidx.ui.graphics) // Graphics components for Compose
    implementation(libs.androidx.ui.tooling.preview) // Preview tooling for Compose
    implementation(libs.androidx.material3) // Material Design 3 components
    implementation(libs.androidx.appcompat) // AppCompat library for backward compatibility
    implementation(libs.androidx.uiautomator) // UI Automator for UI testing
    implementation(libs.androidx.monitor) // AndroidX Test Monitor
    testImplementation(libs.junit) // JUnit for unit testing
    androidTestImplementation(libs.androidx.uiautomator.v220) // UI Automator for UI testing
    androidTestImplementation(libs.androidx.junit) // AndroidX JUnit for UI testing
    androidTestImplementation(libs.androidx.espresso.core) // Espresso for UI testing
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Compose BOM for UI testing
    androidTestImplementation(libs.androidx.ui.test.junit4) // Jetpack Compose UI testing with JUnit
    debugImplementation(libs.androidx.ui.tooling) // Compose UI tooling for debugging
    debugImplementation(libs.androidx.ui.test.manifest) // Compose UI test manifest
}
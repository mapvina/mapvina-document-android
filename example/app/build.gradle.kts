plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.mapvina.mapvinademotest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mapvina.mapvinademotest"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding { enable = true }

    buildFeatures {  viewBinding = true }
}

dependencies {
    implementation("io.github.map-vina:android-sdk:2.0.1")
    implementation("io.github.map-vina:android-sdk-geojson:2.0.1")
    implementation("io.github.map-vina:android-sdk-turf:2.0.1")
    implementation("io.github.map-vina:android-plugin-annotation-v9:2.0.1")
    implementation("io.github.map-vina:libandroid-navigation:2.0.0")
    implementation("io.github.map-vina:libandroid-navigation-ui:2.0.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.basement)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
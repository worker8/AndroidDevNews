plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("androiddevnews.android.library.compose")
}

android {
    namespace = "com.worker8.androiddevnews.common"
    compileSdk = 32

    defaultConfig {
        minSdk = 21
        targetSdk = 32

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    /* material */
    implementation(libs.android.material)
    /* image loading libs */
    implementation(libs.bundles.coil)
}
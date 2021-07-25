import com.worker8.gradle.Secrets

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"
//    buildTypes {
//        getByName("all") {
//            buildConfigField(
//                    "String",
//                    "redditClientId",
//                    "\"${Secrets.redditClientId}\""
//                )
//        }
//    }
    defaultConfig {
        applicationId = "com.worker8.androiddevnews"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "RedditClientId",
                "\"${Secrets.redditClientId}\""
            )
        }
        release {
            buildConfigField(
                "String",
                "RedditClientId",
                "\"${Secrets.redditClientId}\""
            )
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
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = version("compose")
    }
}

dependencies {

    implementation("androidx.core:core-ktx:${version("ktx")}")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.1")
    implementation("androidx.appcompat:appcompat:${version("appCompat")}")
    implementation("com.google.android.material:material:${version("material")}")
    implementation("androidx.compose.ui:ui:${version("compose")}")
    implementation("androidx.compose.material:material:${version("compose")}")
    implementation("androidx.compose.ui:ui-tooling:${version("compose")}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${version("lifecycleRuntimeKtx")}")
    implementation("androidx.activity:activity-compose:${version("activityCompose")}")
//    api("net.dean.jraw:JRAW:1.1.0")
    implementation("com.github.KirkBushman:ARAW:${version("ARAW")}")
    implementation("io.coil-kt:coil-compose:${version("coil")}")
    implementation("io.coil-kt:coil:${version("coil")}")


    testImplementation("junit:junit:${version("junit")}")
    androidTestImplementation("androidx.test.ext:junit:${version("junitExt")}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${version("espresso")}")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${version("compose")}")
}

fun version(key: String) = rootProject.extra[key] as String
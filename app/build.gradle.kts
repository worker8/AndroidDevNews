import com.worker8.gradle.Secrets

plugins {
    id("com.android.application")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
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
    /* android */
    implementation("androidx.core:core-ktx:${version("ktx")}")
    implementation("androidx.appcompat:appcompat:${version("appCompat")}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${version("lifecycleRuntimeKtx")}")
    /* UI libs - material & compose */
    implementation("com.google.android.material:material:${version("material")}")
    implementation("androidx.compose.ui:ui:${version("compose")}")
    implementation("androidx.compose.material:material:${version("compose")}")
    implementation("androidx.compose.ui:ui-tooling:${version("compose")}")
    implementation("androidx.activity:activity-compose:${version("activityCompose")}")
    implementation("androidx.navigation:navigation-compose:${version("navCompose")}")
    /* reddit client */
    implementation("com.github.KirkBushman:ARAW:${version("ARAW")}")
    implementation("com.prof18.rssparser:rssparser:${version("rss")}")
    /* image loading libs */
    implementation("io.coil-kt:coil-compose:${version("coil")}")
    implementation("io.coil-kt:coil:${version("coil")}")
    /* DI */
    implementation("com.google.dagger:hilt-android:${version("hilt")}")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    kapt("com.google.dagger:hilt-android-compiler:${version("hilt")}")

    testImplementation("junit:junit:${version("junit")}")
    androidTestImplementation("androidx.test.ext:junit:${version("junitExt")}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${version("espresso")}")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${version("compose")}")
}

fun version(key: String) = rootProject.extra[key] as String
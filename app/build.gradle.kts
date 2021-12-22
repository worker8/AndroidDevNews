import com.worker8.gradle.Secrets

plugins {
    id("com.android.application")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
}

android {
    compileSdk = 31
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
            isMinifyEnabled = true
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
        kotlinCompilerExtensionVersion = "${version("compose")}"
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
    implementation("androidx.constraintlayout:constraintlayout-compose:${version("constraintLayoutCompose")}")

    /* reddit client */
    implementation("com.github.KirkBushman:ARAW:${version("ARAW")}")
    /* rss */
    implementation("com.prof18.rssparser:rssparser:${version("rssProf18")}")
    implementation("com.icosillion.podengine:podengine:${version("rssPodcastFeed")}")


    /* image loading libs */
    implementation("io.coil-kt:coil-compose:1.3.0")
    implementation("io.coil-kt:coil:1.3.0")
    /* DI */
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    kapt("com.google.dagger:hilt-android-compiler:2.38.1")
    implementation("com.google.dagger:hilt-android:2.38.1")

    /* exoplayer - for podcast */
    implementation("com.google.android.exoplayer:exoplayer:${version("exoplayer")}")
    implementation("com.google.android.exoplayer:exoplayer-core:${version("exoplayer")}")

    /* for notification compat */
    implementation("androidx.media:media:1.4.3")
//    implementation("com.google.android.exoplayer:extension-mediasession:${version("exoplayer")}")
//    implementation("com.google.android.exoplayer:exoplayer-ui:${version("exoplayer")}")

    testImplementation("junit:junit:${version("junit")}")
    androidTestImplementation("androidx.test.ext:junit:${version("junitExt")}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${version("espresso")}")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${version("compose")}")
}

fun version(key: String) = rootProject.extra[key] as String
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
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "${version("compose")}"
    }
}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}

dependencies {
    /* android */
    implementation("androidx.core:core-ktx:${version("ktx")}")
    implementation("androidx.appcompat:appcompat:${version("appCompat")}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${version("lifecycleRuntimeKtx")}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${version("lifecycleRuntimeKtx")}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${version("lifecycleRuntimeKtx")}")

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
    implementation("io.coil-kt:coil-compose:${version("coil")}")
    implementation("io.coil-kt:coil:${version("coil")}")
    /* DI */
    kapt("com.google.dagger:hilt-android-compiler:${version("hiltDagger")}")
    implementation("com.google.dagger:hilt-android:${version("hiltDagger")}")

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
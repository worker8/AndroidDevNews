import com.worker8.gradle.Secrets

plugins {
    id("com.android.application")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
}

android {
    compileSdk = 33
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
        targetSdk = 32
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
        freeCompilerArgs += "-Xjvm-default=compatibility"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libraries.versions.compose.get()
    }
}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}

dependencies {
    /* android */
    implementation(libraries.androidx.appcompat)
    implementation(libraries.androidx.core.ktx)
    implementation(libraries.bundles.lifecycle)

    /* UI libs - material & compose */
    implementation(libraries.android.material)
    implementation(libraries.bundles.compose)

    /* reddit client */
    implementation(libraries.araw)

    /* rss */
    implementation(libraries.rssProf18)
    implementation(libraries.rssPodcastFeed)

    /* image loading libs */
    implementation(libraries.bundles.coil)

    /* DI */
    kapt(libraries.dagger.hilt.compiler)
    implementation(libraries.dagger.hilt.android)

    /* exoplayer - for podcast */
    implementation(libraries.bundles.exoplayer)

    /* for notification compat */
    implementation(libraries.androidx.media)
//    implementation("com.google.android.exoplayer:extension-mediasession:${version("exoplayer")}")
//    implementation("com.google.android.exoplayer:exoplayer-ui:${version("exoplayer")}")

    testImplementation(libraries.test.junit)
    testImplementation(libraries.test.junit.ext)
    testImplementation(libraries.test.espresso)
    testImplementation(libraries.test.compose.ui)
}

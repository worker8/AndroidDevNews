import com.worker8.gradle.Secrets

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("androiddevnews.android.hilt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.worker8.androiddevnews"
    compileSdk = 34
    buildFeatures {
        buildConfig = true
    }
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
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        freeCompilerArgs += "-Xjvm-default=all-compatibility"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(project(":reddit"))
    implementation(project(":common"))
    /* android */
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.lifecycle)

    /* UI libs - material & compose */
    implementation(libs.android.material)
    implementation(libs.bundles.compose)

    /* reddit client */
    implementation(libs.araw)

    /* rss */
    implementation(libs.rssProf18)
    implementation(libs.rssPodcastFeed)

    /* image loading libs */
    implementation(libs.bundles.coil)

    /* exoplayer - for podcast */
    implementation(libs.bundles.exoplayer)

    /* browser - for chrome custom tabs */
    implementation(libs.androidx.browser)

    /* for notification compat */
    implementation(libs.androidx.media)
//    implementation("com.google.android.exoplayer:extension-mediasession:${version("exoplayer")}")
//    implementation("com.google.android.exoplayer:exoplayer-ui:${version("exoplayer")}")

    testImplementation(libs.test.junit)
    testImplementation(libs.test.junit.ext)
    testImplementation(libs.test.espresso)
    testImplementation(libs.test.compose.ui)
}

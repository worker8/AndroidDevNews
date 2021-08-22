// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    /* versions */
    val compose by extra("1.0.0-beta09")
    val constraintLayoutcompose by extra("1.0.0-beta02")
    val coil by extra("1.3.0")
    val activityCompose by extra("1.3.0-rc01")
    val navCompose by extra("2.4.0-alpha05")
    val ktx by extra("1.6.0")
    val appCompat by extra("1.3.0")
    val material by extra("1.4.0")
    val lifecycleRuntimeKtx by extra("2.3.1")
    val ARAW by extra("f0171e78e4")
    val rssProf18 by extra("3.1.5")
    val rssPodcastFeed by extra("2.4.1")
    /* test versions */
    val junit by extra("4.13.2")
    val junitExt by extra("1.1.3")
    val espresso by extra("3.4.0")
    val hilt by extra("2.37")
    val exoplayer by extra("2.15.0")

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-beta05")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.37")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}
//subprojects {
//    configure<BaseExtension> {
//        buildTypes {
//            all {
//                buildConfigField(
//                    "String",
//                    "redditClientId",
//                    "\"${Secrets.redditClientId}\""
//                )
//            }
//        }
//    }
//}
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

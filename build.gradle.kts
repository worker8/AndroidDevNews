// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    /* versions */
    val compose by extra("1.0.5")
    val constraintLayoutCompose by extra("1.0.0-rc02")
    val activityCompose by extra("1.4.0")
    val navCompose by extra("2.4.0-beta02")
    val coil by extra("1.3.0")
    val appCompat by extra("1.4.0")
    val material by extra("1.4.0")
    val ktx by extra("1.7.0")
    val lifecycleRuntimeKtx by extra("2.4.0")
    val ARAW by extra("f0171e78e4")
    val rssProf18 by extra("3.1.5")
    val rssPodcastFeed by extra("2.4.1")
    /* test versions */
    val junit by extra("4.13.2")
    val junitExt by extra("1.1.3")
    val espresso by extra("3.4.0")
    val hilt by extra("2.38.1")
    val exoplayer by extra("2.15.0")

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-beta05")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.38.1")

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

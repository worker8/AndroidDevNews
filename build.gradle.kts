plugins {
    id("com.github.ben-manes.versions") version ("0.42.0")
    id("nl.littlerobots.version-catalog-update") version ("0.5.3")
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.classpath.android.gradlePlugin)
//        classpath("com.android.tools.build:gradle:7.3.0")
        classpath(libs.classpath.dagger.hilt.gradlePlugin)
//        classpath("com.google.dagger:hilt-android-gradle-plugin:2.44")
        classpath(libs.classpath.kotlin.gradlePlugin)
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        classpath("nl.littlerobots.vcu:plugin:0.5.3")
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

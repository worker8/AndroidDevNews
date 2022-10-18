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
        classpath(libraries.classpath.android.gradlePlugin)
        classpath(libraries.classpath.dagger.hilt.gradlePlugin)
        classpath(libraries.classpath.kotlin.gradlePlugin)
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

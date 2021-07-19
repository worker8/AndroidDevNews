
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val compose_version by extra("1.0.0-beta09")
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-beta05")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")

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
//fun <T> NamedDomainObjectContainer<T>.all(configure: T.() -> Unit) = getByName("all", configure)
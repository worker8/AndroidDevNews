plugins {
    `kotlin-dsl`
}

group = "com.worker8.androiddevnews.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compileOnly(libs.classpath.android.gradlePlugin)
    compileOnly(libs.classpath.kotlin.gradlePlugin)
}


gradlePlugin {
    plugins {
        register("androidLibraryCompose") {
            id = "androiddevnews.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
    }
}
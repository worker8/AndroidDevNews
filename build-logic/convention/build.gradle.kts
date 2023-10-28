plugins {
    `kotlin-dsl`
}

group = "com.worker8.androiddevnews.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
        register("androidHilt") {
            id = "androiddevnews.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
    }
}
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
includeBuild("build-logic")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
//        jcenter() // Warning: this repository is going to shut down soon
    }
//    versionCatalogs {
//        create("libraries") {
//            from(files("./gradle/libs.versions.toml"))
//        }
//    }
}
rootProject.name = "AndroidDevNews"
include(":app")
include(":reddit")
include(":common")

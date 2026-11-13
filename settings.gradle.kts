pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "VirginActive"

include(
    ":app",
    ":core:model",
    ":core:domain",
    ":core:network",
    ":core:data",
    ":core:designsystem",
    ":core:ui")

include(":features:login", ":features:home", ":features:timetable", ":features:booking")

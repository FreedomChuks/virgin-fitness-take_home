import java.util.Properties

plugins {
    id("vactive.android.application")
    id("vactive.android.compose")
    id("vactive.android.hilt")
    id("vactive.serialization")
}

val localApiBaseUrl =
    providers
        .fileContents(rootProject.layout.projectDirectory.file("local.properties"))
        .asText
        .map { text ->
            Properties()
                .apply { load(text.reader()) }
                .getProperty("apiBaseUrl", "http://10.0.2.2:8080/")
        }
val apiBaseUrl =
    providers.gradleProperty("apiBaseUrl").orElse(localApiBaseUrl).orElse("http://10.0.2.2:8080/")

android {
    namespace = "com.virginactive.app"
    defaultConfig {
        applicationId = "com.virginactive.assessment"
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "API_BASE_URL", "\"${apiBaseUrl.get()}\"")
    }
    buildFeatures { buildConfig = true }
}

dependencies {
    implementation(libs.hilt.compose)
    implementation(libs.okhttp)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.coroutines.android)
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(libs.activity.compose)
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.lifecycle.navigation3)
    implementation(project(":core:data"))
    implementation(project(":core:network"))
    implementation(project(":features:login"))
    implementation(project(":features:home"))
    implementation(project(":features:timetable"))
    implementation(project(":features:booking"))
}

plugins {
    id("vactive.android.library")
    id("vactive.android.compose")
}

android { namespace = "com.virginactive.core.ui" }

dependencies {
    implementation(libs.coroutines.android)
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:designsystem"))
}

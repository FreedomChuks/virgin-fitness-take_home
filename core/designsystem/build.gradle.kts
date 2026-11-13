plugins {
    id("vactive.android.library")
    id("vactive.android.compose")
}

android { namespace = "com.virginactive.core.designsystem" }

dependencies {
    // Icons are part of the design language, so modules that use the system receive them too.
    api(libs.compose.icons)
}

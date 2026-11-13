plugins {
    id("vactive.jvm.library")
    id("vactive.serialization")
}

dependencies {
    implementation(libs.coroutines)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)
    testImplementation(libs.mockwebserver)
}

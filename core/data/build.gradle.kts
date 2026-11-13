plugins { id("vactive.jvm.dagger") }

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(libs.coroutines)
    testImplementation(libs.json)
    testImplementation(libs.mockwebserver)
}

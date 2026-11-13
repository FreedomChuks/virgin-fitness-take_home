plugins { id("vactive.jvm.library") }

dependencies {
    implementation(project(":core:model"))
    implementation(libs.coroutines)
}

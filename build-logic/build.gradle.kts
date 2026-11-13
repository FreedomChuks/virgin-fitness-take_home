plugins { `kotlin-dsl` }

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.compose.gradle.plugin)
    implementation(libs.serialization.gradle.plugin)
    implementation(libs.hilt.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
    implementation(libs.spotless.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("AndroidApplication") {
            id = "vactive.android.application"
            implementationClass = "com.virginactive.buildlogic.AndroidApplicationConventionPlugin"
        }
        register("AndroidLibrary") {
            id = "vactive.android.library"
            implementationClass = "com.virginactive.buildlogic.AndroidLibraryConventionPlugin"
        }
        register("AndroidCompose") {
            id = "vactive.android.compose"
            implementationClass = "com.virginactive.buildlogic.AndroidComposeConventionPlugin"
        }
        register("AndroidFeature") {
            id = "vactive.android.feature"
            implementationClass = "com.virginactive.buildlogic.AndroidFeatureConventionPlugin"
        }
        register("AndroidHilt") {
            id = "vactive.android.hilt"
            implementationClass = "com.virginactive.buildlogic.AndroidHiltConventionPlugin"
        }
        register("JvmLibrary") {
            id = "vactive.jvm.library"
            implementationClass = "com.virginactive.buildlogic.JvmLibraryConventionPlugin"
        }
        register("JvmDagger") {
            id = "vactive.jvm.dagger"
            implementationClass = "com.virginactive.buildlogic.JvmDaggerConventionPlugin"
        }
        register("Serialization") {
            id = "vactive.serialization"
            implementationClass = "com.virginactive.buildlogic.SerializationConventionPlugin"
        }
        register("Formatting") {
            id = "vactive.formatting"
            implementationClass = "com.virginactive.buildlogic.FormattingConventionPlugin"
        }
    }
}

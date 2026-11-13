package com.virginactive.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.library(alias: String) = libs.findLibrary(alias).get()

internal fun Project.unitTesting() {
    dependencies.add("testImplementation", library("junit"))
    dependencies.add("testImplementation", library("coroutines-test"))
}

internal fun CommonExtension.androidDefaults() {
    compileSdk = 37
    defaultConfig.minSdk = 26
    compileOptions.sourceCompatibility = JavaVersion.VERSION_17
    compileOptions.targetCompatibility = JavaVersion.VERSION_17
    buildFeatures.buildConfig = false
}

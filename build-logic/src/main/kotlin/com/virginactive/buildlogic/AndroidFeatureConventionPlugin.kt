package com.virginactive.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            pluginManager.apply("vactive.android.library")
            pluginManager.apply("vactive.android.compose")
            pluginManager.apply("vactive.android.hilt")
            listOf("lifecycle-runtime", "lifecycle-viewmodel", "coroutines-android").forEach {
                dependencies.add("implementation", library(it))
            }
            listOf(":core:model", ":core:domain", ":core:designsystem", ":core:ui").forEach {
                dependencies.add("implementation", project(it))
            }

            Unit
        }
}

package com.virginactive.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            fun configureCompose() {
                extensions.getByType<CommonExtension>().buildFeatures.compose = true
                dependencies.add("implementation", dependencies.platform(library("compose-bom")))
                listOf("compose-ui", "compose-material3", "compose-preview").forEach {
                    dependencies.add("implementation", library(it))
                }
                dependencies.add("debugImplementation", library("compose-tooling"))
            }
            pluginManager.withPlugin("com.android.application") { configureCompose() }
            pluginManager.withPlugin("com.android.library") { configureCompose() }
        }
}

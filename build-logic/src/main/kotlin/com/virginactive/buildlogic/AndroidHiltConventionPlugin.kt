package com.virginactive.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            pluginManager.apply("com.google.dagger.hilt.android")
            pluginManager.apply("com.google.devtools.ksp")
            dependencies.add("implementation", library("hilt-android"))
            dependencies.add("ksp", library("hilt-compiler"))
        }
}

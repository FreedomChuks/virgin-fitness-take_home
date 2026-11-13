package com.virginactive.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class JvmDaggerConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            pluginManager.apply("vactive.jvm.library")
            pluginManager.apply("com.google.devtools.ksp")
            dependencies.add("implementation", library("javax-inject"))
            dependencies.add("implementation", library("dagger-runtime"))
            dependencies.add("ksp", library("dagger-compiler"))

            Unit
        }
}

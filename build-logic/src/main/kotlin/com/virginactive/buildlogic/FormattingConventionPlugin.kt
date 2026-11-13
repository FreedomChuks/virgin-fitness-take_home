package com.virginactive.buildlogic

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class FormattingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            pluginManager.apply("base")
            pluginManager.apply("com.diffplug.spotless")
            extensions.configure<SpotlessExtension> {
                lineEndings = com.diffplug.spotless.LineEnding.UNIX
                kotlin {
                    target("**/src/**/*.kt")
                    targetExclude("**/build/**", "**/.gradle/**", "**/.kotlin/**")
                    ktfmt(libs.findVersion("ktfmt").get().requiredVersion).kotlinlangStyle()
                    trimTrailingWhitespace()
                    endWithNewline()
                }
                kotlinGradle {
                    target("*.gradle.kts", "**/*.gradle.kts")
                    targetExclude("**/build/**", "**/.gradle/**", "**/.kotlin/**")
                    ktfmt(libs.findVersion("ktfmt").get().requiredVersion).kotlinlangStyle()
                    trimTrailingWhitespace()
                    endWithNewline()
                }
                format("misc") {
                    target(
                        "README.md",
                        ".editorconfig",
                        "gradle.properties",
                        "gradle/**/*.toml",
                        "**/src/**/*.xml")
                    targetExclude("**/build/**")
                    trimTrailingWhitespace()
                    endWithNewline()
                    Unit
                }
            }

            tasks.named("check") { dependsOn("spotlessCheck") }

            Unit
        }
}

package com.virginactive.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@ActivePreviews
@Composable
private fun ComponentsPreview() {
    ActiveTheme {
        Surface {
            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(ActiveSpace.screen),
                verticalArrangement = Arrangement.spacedBy(ActiveSpace.medium)) {
                    ActiveBrandMark()
                    TypeScale()
                    Swatches()
                    ActiveHero("Crush your goals this week", "One more class to hit your target.")
                    ActiveCard(Modifier.fillMaxWidth(), onClick = {}) {
                        Text("Deep Stretch Yoga", style = MaterialTheme.typography.titleMedium)
                        Text("07:00 – 08:00", style = MaterialTheme.typography.bodyMedium)
                        ActiveBadge(
                            "Booked", tone = ActiveStatusTone.Success, icon = ActiveIcons.Success)
                    }
                    ActiveNotice(
                        "Couldn't connect. Pull down to try again.", tone = ActiveStatusTone.Error)
                    ActiveButton("Book class", {}, Modifier.fillMaxWidth())
                    ActiveSecondaryButton("Add to calendar", {}, Modifier.fillMaxWidth())
                    ActiveTextButton("Cancel booking", {}, destructive = true)
                }
        }
    }
}

@Composable
private fun TypeScale() {
    val type = MaterialTheme.typography
    Column(verticalArrangement = Arrangement.spacedBy(ActiveSpace.xs)) {
        Text("Headline large", style = type.headlineLarge)
        Text("Headline small", style = type.headlineSmall)
        Text("Title large", style = type.titleLarge)
        Text("Title medium", style = type.titleMedium)
        Text("Body large — readable class detail copy.", style = type.bodyLarge)
        Text("Body medium — supporting text.", style = type.bodyMedium)
        Text("LABEL MEDIUM", style = type.labelMedium)
    }
}

@Composable
private fun Swatches() {
    val scheme = MaterialTheme.colorScheme
    val status = ActiveTheme.status
    Row(horizontalArrangement = Arrangement.spacedBy(ActiveSpace.xs)) {
        listOf(
                scheme.primary,
                scheme.surface,
                scheme.surfaceContainer,
                scheme.onSurface,
                status.success.container,
                status.waitlist.container,
                status.unavailable.container,
                status.error.container,
                status.info.container)
            .forEach { Swatch(it) }
    }
}

@Composable
private fun Swatch(color: Color) {
    Box(
        Modifier.size(28.dp)
            .background(MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.extraSmall)
            .padding(1.dp)
            .background(color, MaterialTheme.shapes.extraSmall))
}

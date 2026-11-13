package com.virginactive.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class ActiveCardStyle {
    Outlined,
    Tonal,
}

@Composable
fun ActiveCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    style: ActiveCardStyle =
        if (onClick != null) ActiveCardStyle.Outlined else ActiveCardStyle.Tonal,
    contentPadding: PaddingValues = PaddingValues(ActiveSpace.medium),
    content: @Composable ColumnScope.() -> Unit
) {
    val body: @Composable ColumnScope.() -> Unit = {
        Column(
            Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(ActiveSpace.small),
            content = content)
    }
    val colors =
        CardDefaults.cardColors(
            containerColor =
                when (style) {
                    ActiveCardStyle.Outlined -> MaterialTheme.colorScheme.surfaceContainerLowest
                    ActiveCardStyle.Tonal -> MaterialTheme.colorScheme.surfaceContainer
                },
            contentColor = MaterialTheme.colorScheme.onSurface)
    val border =
        if (style == ActiveCardStyle.Outlined)
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        else null
    if (onClick == null) {
        Card(
            modifier = modifier,
            shape = MaterialTheme.shapes.large,
            colors = colors,
            border = border,
            content = body)
    } else {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = MaterialTheme.shapes.large,
            colors = colors,
            border = border,
            content = body)
    }
}

@ActivePreviews
@Composable
private fun ActiveCardPreview() {
    ActiveTheme {
        Surface {
            Column(
                Modifier.padding(ActiveSpace.medium),
                verticalArrangement = Arrangement.spacedBy(ActiveSpace.medium)) {
                    ActiveCard(Modifier.fillMaxWidth(), onClick = {}) {
                        Text("Deep Stretch Yoga", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "07:00 – 08:00 · Sea Point",
                            style = MaterialTheme.typography.bodyMedium)
                    }
                    ActiveCard(Modifier.fillMaxWidth()) {
                        Text("Your goals", style = MaterialTheme.typography.titleMedium)
                        Text("3 of 4 this week", style = MaterialTheme.typography.bodyMedium)
                    }
                }
        }
    }
}

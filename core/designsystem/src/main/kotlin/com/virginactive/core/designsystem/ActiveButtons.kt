package com.virginactive.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ActiveButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    busy: Boolean = false,
    leadingIcon: ImageVector? = null,
) {
    val colors =
        if (busy)
            ButtonDefaults.buttonColors(
                disabledContainerColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.onPrimary)
        else ButtonDefaults.buttonColors()
    Button(
        onClick = onClick,
        enabled = enabled && !busy,
        modifier =
            modifier.heightIn(min = ActiveSize.button).semantics {
                if (busy) stateDescription = "In progress"
            },
        shape = CircleShape,
        colors = colors,
        contentPadding =
            PaddingValues(horizontal = ActiveSpace.large, vertical = ActiveSpace.smallMedium)) {
            ButtonContent(label, if (busy) null else leadingIcon, busy)
        }
}

@Composable
internal fun ButtonContent(label: String, icon: ImageVector?, busy: Boolean = false) {
    when {
        busy -> {
            CircularProgressIndicator(
                Modifier.size(ActiveSize.iconSmall),
                color = LocalContentColor.current,
                strokeWidth = ActiveSpace.xxs)
            Spacer(Modifier.width(ActiveSpace.small))
        }
        icon != null -> {
            Icon(icon, contentDescription = null, modifier = Modifier.size(ActiveSize.icon))
            Spacer(Modifier.width(ActiveSpace.small))
        }
    }
    Text(label, style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
}

@ActivePreviews
@Composable
private fun ActiveButtonPreview() {
    ActiveTheme {
        Surface {
            Column(
                Modifier.padding(ActiveSpace.medium),
                verticalArrangement = Arrangement.spacedBy(ActiveSpace.smallMedium)) {
                    ActiveButton("Book class", {}, Modifier.fillMaxWidth())
                    ActiveButton(
                        "Add to calendar",
                        {},
                        Modifier.fillMaxWidth(),
                        leadingIcon = ActiveIcons.Event)
                    ActiveButton("Booking…", {}, Modifier.fillMaxWidth(), busy = true)
                    ActiveButton("Unavailable", {}, Modifier.fillMaxWidth(), enabled = false)
                }
        }
    }
}

@Composable
fun ActiveSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = ActiveSize.button),
        shape = CircleShape,
        border =
            BorderStroke(
                1.dp,
                if (enabled) MaterialTheme.colorScheme.outline
                else MaterialTheme.colorScheme.outlineVariant),
        colors =
            ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
        contentPadding =
            PaddingValues(horizontal = ActiveSpace.large, vertical = ActiveSpace.smallMedium)) {
            ButtonContent(label, leadingIcon)
        }
}

@ActivePreviews
@Composable
private fun ActiveSecondaryButtonPreview() {
    ActiveTheme {
        Surface {
            Column(
                Modifier.padding(ActiveSpace.medium),
                verticalArrangement = Arrangement.spacedBy(ActiveSpace.smallMedium)) {
                    ActiveSecondaryButton("Show all days", {}, Modifier.fillMaxWidth())
                    ActiveSecondaryButton(
                        "Add to calendar",
                        {},
                        Modifier.fillMaxWidth(),
                        leadingIcon = ActiveIcons.Event)
                    ActiveSecondaryButton("Disabled", {}, Modifier.fillMaxWidth(), enabled = false)
                }
        }
    }
}

@Composable
fun ActiveTextButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    destructive: Boolean = false,
    trailingIcon: ImageVector? = null,
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = ActiveSize.minTouch),
        shape = CircleShape,
        colors =
            ButtonDefaults.textButtonColors(
                contentColor =
                    if (destructive) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary)) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            if (trailingIcon != null) {
                Icon(
                    trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.padding(start = ActiveSpace.xs).size(ActiveSize.icon))
            }
        }
}

@ActivePreviews
@Composable
private fun ActiveTextButtonPreview() {
    ActiveTheme {
        Surface {
            Column(Modifier.padding(ActiveSpace.medium)) {
                ActiveTextButton("View all", {}, trailingIcon = ActiveIcons.Forward)
                ActiveTextButton("Cancel booking", {}, destructive = true)
                ActiveTextButton("Disabled", {}, enabled = false)
            }
        }
    }
}

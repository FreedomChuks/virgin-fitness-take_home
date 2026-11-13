package com.virginactive.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.unit.dp

@Composable
fun ActiveDateChip(
    topLabel: String,
    mainLabel: String,
    selected: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    marked: Boolean = false,
) {
    val container =
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceContainerLowest
    val content =
        if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Surface(
        selected = selected,
        onClick = onClick,
        modifier =
            modifier.widthIn(min = 52.dp).heightIn(min = 64.dp).clearAndSetSemantics {
                this.contentDescription = contentDescription
                this.selected = selected
                role = Role.Tab
                onClick {
                    onClick()
                    true
                }
            },
        shape = MaterialTheme.shapes.medium,
        color = container,
        contentColor = content,
        border =
            if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
            Column(
                Modifier.padding(
                    horizontal = ActiveSpace.smallMedium, vertical = ActiveSpace.small),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(ActiveSpace.xxs)) {
                    Text(
                        topLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color =
                            if (selected) content else MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(mainLabel, style = MaterialTheme.typography.titleMedium)
                    Box(
                        Modifier.size(5.dp)
                            .background(
                                when {
                                    !marked -> Color.Transparent
                                    selected -> content
                                    else -> MaterialTheme.colorScheme.primary
                                },
                                CircleShape))
                }
        }
}

@ActivePreviews
@Composable
private fun ActiveDateChipPreview() {
    ActiveTheme {
        Surface {
            Row(
                Modifier.padding(ActiveSpace.medium),
                horizontalArrangement = Arrangement.spacedBy(ActiveSpace.small)) {
                    ActiveDateChip("Week", "All", false, {}, "All days")
                    ActiveDateChip("Thu", "12", false, {}, "Thursday 12 November")
                    ActiveDateChip("Fri", "13", true, {}, "Friday 13 November", marked = true)
                    ActiveDateChip("Sat", "14", false, {}, "Saturday 14 November")
                }
        }
    }
}

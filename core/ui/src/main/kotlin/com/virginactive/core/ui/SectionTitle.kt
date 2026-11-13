package com.virginactive.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import com.virginactive.core.designsystem.*

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
) {
    Row(
        modifier.fillMaxWidth().heightIn(min = ActiveSize.minTouch),
        verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f).semantics(mergeDescendants = true) { heading() }) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                if (supporting != null) {
                    Text(
                        supporting,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (actionLabel != null) {
                ActiveTextButton(actionLabel, onAction, trailingIcon = ActiveIcons.Forward)
            }
        }
}

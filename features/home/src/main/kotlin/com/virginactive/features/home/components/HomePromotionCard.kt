package com.virginactive.features.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.virginactive.core.designsystem.*
import com.virginactive.core.model.HomeBlock

@Composable
internal fun HomePromotionCard(block: HomeBlock.Promotion, modifier: Modifier = Modifier) {
    ActiveCard(modifier.fillMaxWidth()) {
        Text(
            "PROMOTION",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary)
        Text(
            block.title,
            Modifier.semantics { heading() },
            style = MaterialTheme.typography.titleLarge)
        block.subtitle?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

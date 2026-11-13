package com.virginactive.features.booking.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.virginactive.core.designsystem.*
import com.virginactive.core.model.FitnessClass
import com.virginactive.core.ui.*

@Composable
internal fun ClassHeader(fitnessClass: FitnessClass, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(ActiveSpace.xs)) {
        Text(
            fitnessClass.categoryLabel.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary)
        Text(
            fitnessClass.title,
            Modifier.semantics { heading() },
            style = MaterialTheme.typography.headlineLarge)
        Text(
            "with ${fitnessClass.trainer}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

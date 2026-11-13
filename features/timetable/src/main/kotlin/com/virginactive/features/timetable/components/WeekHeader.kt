package com.virginactive.features.timetable.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.virginactive.core.designsystem.*
import com.virginactive.core.model.Timetable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
internal fun WeekHeader(table: Timetable, timezone: String?, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(
                start = ActiveSpace.screen,
                end = ActiveSpace.screen,
                top = ActiveSpace.small,
                bottom = ActiveSpace.xs),
        verticalArrangement = Arrangement.spacedBy(ActiveSpace.xs)) {
            Text(
                weekRange(table.weekStart, table.weekEnd),
                Modifier.semantics { heading() },
                style = MaterialTheme.typography.headlineSmall)
            Text(
                if (timezone != null) "Times shown in club time · $timezone"
                else "Times shown in club time",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
}

internal fun weekRange(start: LocalDate, end: LocalDate): String {
    val full = DateTimeFormatter.ofPattern("d MMMM yyyy")
    return when {
        start.year != end.year ->
            "${start.format(DateTimeFormatter.ofPattern("d MMM yyyy"))} – ${end.format(full)}"
        start.month != end.month ->
            "${start.format(DateTimeFormatter.ofPattern("d MMM"))} – ${end.format(DateTimeFormatter.ofPattern("d MMM yyyy"))}"
        else -> "${start.dayOfMonth} – ${end.format(full)}"
    }
}

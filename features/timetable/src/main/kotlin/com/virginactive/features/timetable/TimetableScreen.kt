package com.virginactive.features.timetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.virginactive.core.designsystem.*
import com.virginactive.core.model.*
import com.virginactive.core.ui.*
import com.virginactive.features.timetable.components.*
import java.time.format.DateTimeFormatter

private val dayTitle = DateTimeFormatter.ofPattern("EEEE d MMMM")
private val dayName = DateTimeFormatter.ofPattern("EEEE")

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimetableScreen(state: TimetableState, onAction: (TimetableAction) -> Unit) {
    val now = rememberCurrentInstant()
    RefreshableStateContent(state.content, { onAction(TimetableAction.Refresh) }) { table ->
        val selected = state.selectedDate
        val visibleDays = table.days.filter { selected == null || it.date == selected }
        val timezone = table.days.firstNotNullOfOrNull { it.classes.firstOrNull()?.timezone }
        val dayListState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = ActiveSpace.extraLarge)) {
                item(key = "week") { WeekHeader(table, timezone) }
                stickyHeader(key = "days") {
                    DaySelector(
                        table,
                        selected,
                        onSelect = { onAction(TimetableAction.SelectDay(it)) },
                        listState = dayListState)
                }
                visibleDays.forEach { day ->
                    val date = day.date
                    item(key = "day-${day.date}") {
                        SectionTitle(
                            date.format(dayTitle),
                            Modifier.padding(
                                start = ActiveSpace.screen,
                                end = ActiveSpace.screen,
                                top = ActiveSpace.medium,
                                bottom = ActiveSpace.xs),
                            supporting =
                                listOfNotNull(
                                        classCount(day.classes.size),
                                        "Today at the club"
                                            .takeIf { day.date == table.selectedDate })
                                    .joinToString(" · "))
                    }
                    if (day.classes.isEmpty()) {
                        item(key = "empty-${day.date}") {
                            if (selected == null) {
                                Text(
                                    "No classes scheduled.",
                                    Modifier.padding(horizontal = ActiveSpace.screen),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                ActiveEmptyState(
                                    "No classes on ${date.format(dayName)}",
                                    "Try another day, or browse the whole week.",
                                    Modifier.fillMaxWidth(),
                                    icon = ActiveIcons.Timetable,
                                    actionLabel = "Show all days",
                                    onAction = { onAction(TimetableAction.SelectDay(null)) })
                            }
                        }
                    }
                    items(day.classes, key = { it.ref.classId }) { fitnessClass ->
                        ClassCard(
                            fitnessClass,
                            now,
                            { onAction(TimetableAction.OpenClass(fitnessClass.ref)) },
                            Modifier.padding(
                                horizontal = ActiveSpace.screen, vertical = ActiveSpace.xs))
                    }
                }
            }
    }
}

private fun classCount(count: Int) =
    when (count) {
        0 -> null
        1 -> "1 class"
        else -> "$count classes"
    }

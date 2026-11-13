package com.virginactive.features.timetable.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.virginactive.core.designsystem.*
import com.virginactive.core.model.Timetable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val weekday = DateTimeFormatter.ofPattern("EEE")
private val spoken = DateTimeFormatter.ofPattern("EEEE d MMMM")

@Composable
internal fun DaySelector(
    table: Timetable,
    selected: LocalDate?,
    onSelect: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    LaunchedEffect(selected, table.weekStart) {
        val index = table.days.indexOfFirst { it.date == selected } + 1
        if (index > 0) listState.animateScrollToItem((index - 2).coerceAtLeast(0))
    }
    Surface(modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface) {
        Column {
            LazyRow(
                state = listState,
                contentPadding =
                    PaddingValues(horizontal = ActiveSpace.screen, vertical = ActiveSpace.small),
                horizontalArrangement = Arrangement.spacedBy(ActiveSpace.small)) {
                    item(key = "all") {
                        ActiveDateChip(
                            topLabel = "Week",
                            mainLabel = "All",
                            selected = selected == null,
                            onClick = { onSelect(null) },
                            contentDescription = "All days this week")
                    }
                    items(table.days, key = { it.date.toString() }) { day ->
                        val date = day.date
                        val today = day.date == table.selectedDate
                        ActiveDateChip(
                            topLabel = date.format(weekday),
                            mainLabel = date.dayOfMonth.toString(),
                            selected = selected == day.date,
                            onClick = { onSelect(day.date) },
                            contentDescription =
                                buildString {
                                    append(date.format(spoken))
                                    if (today) append(", today at the club")
                                    append(
                                        when (day.classes.size) {
                                            0 -> ", no classes"
                                            1 -> ", 1 class"
                                            else -> ", ${day.classes.size} classes"
                                        })
                                },
                            marked = today)
                    }
                }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

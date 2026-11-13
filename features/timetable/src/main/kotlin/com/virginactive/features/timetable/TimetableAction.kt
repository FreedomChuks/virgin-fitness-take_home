package com.virginactive.features.timetable

import com.virginactive.core.model.ClassRef
import java.time.LocalDate

sealed interface TimetableAction {
    data object Refresh : TimetableAction

    data class SelectDay(val date: LocalDate?) : TimetableAction

    data class OpenClass(val ref: ClassRef) : TimetableAction
}

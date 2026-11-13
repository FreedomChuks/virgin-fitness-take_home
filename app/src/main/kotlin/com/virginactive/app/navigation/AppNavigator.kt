package com.virginactive.app.navigation

import androidx.navigation3.runtime.NavKey

internal class AppNavigator(private val backStack: MutableList<NavKey>) {
    fun back() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    fun home() {
        while (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    fun timetable(clubId: String? = null) {
        val existing =
            backStack.indexOfLast {
                it is Destination.Timetable && (clubId == null || it.clubId == clubId)
            }
        if (existing >= 0) {
            while (backStack.lastIndex > existing) backStack.removeAt(backStack.lastIndex)
            return
        }
        home()
        backStack.add(Destination.Timetable(clubId))
    }

    fun booking(clubId: String, classId: String) {
        val destination = Destination.Booking(clubId, classId)
        if (backStack.lastOrNull() != destination) backStack.add(destination)
    }
}

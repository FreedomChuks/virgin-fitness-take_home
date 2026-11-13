package com.virginactive.app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class AppNavigatorTest {
    @Test
    fun `system back never removes the root entry`() {
        val stack = mutableListOf<NavKey>(Destination.Home)
        val navigator = AppNavigator(stack)
        navigator.back()
        assertEquals(listOf(Destination.Home), stack)
        navigator.booking("club", "class")
        navigator.back()
        assertEquals(listOf(Destination.Home), stack)
    }

    @Test
    fun `repeated tab selection does not accumulate screens`() {
        val stack = mutableListOf<NavKey>(Destination.Home)
        val navigator = AppNavigator(stack)
        navigator.timetable("club")
        navigator.timetable("club")
        navigator.booking("club", "class")
        navigator.home()
        assertEquals(listOf(Destination.Home), stack)
        navigator.timetable("other")
        assertEquals(listOf(Destination.Home, Destination.Timetable("other")), stack)
    }

    @Test
    fun `classes tab reuses a timetable opened from home`() {
        val stack = mutableListOf<NavKey>(Destination.Home)
        val navigator = AppNavigator(stack)
        navigator.timetable("club")
        navigator.booking("club", "class")
        navigator.timetable()
        assertEquals(listOf(Destination.Home, Destination.Timetable("club")), stack)
        navigator.timetable("other")
        assertEquals(listOf(Destination.Home, Destination.Timetable("other")), stack)
    }

    @Test
    fun `duplicate class taps add only one destination`() {
        val stack = mutableListOf<NavKey>(Destination.Home)
        val navigator = AppNavigator(stack)
        repeat(2) { navigator.booking("club", "definition::2026-11-13") }
        assertEquals(2, stack.size)
    }

    @Test
    fun `route serialization preserves special characters without URL encoding`() {
        val routes: List<Destination> =
            listOf(
                Destination.Login,
                Destination.Home,
                Destination.Timetable(),
                Destination.Booking("club / one", "definition::2026-11-13?&"))
        assertEquals(routes, Json.decodeFromString<List<Destination>>(Json.encodeToString(routes)))
    }
}

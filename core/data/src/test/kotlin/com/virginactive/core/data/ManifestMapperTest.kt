package com.virginactive.core.data

import com.virginactive.core.model.*
import com.virginactive.core.network.dto.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test

class ManifestMapperTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `unknown and malformed blocks do not destroy known content or order`() {
        val manifest =
            json
                .decodeFromString<ManifestDto>(
                    """{"blocks":[
            {"type":"greeting","title":"Hello"},
            {"type":"future","payload":{"anything":true}},
            {"type":"hero","subtitle":"missing title"},
            {"type":"promotion","title":"Welcome"},
            {"type":"myGoals","title":"Goals","items":[{"id":"1","title":"Start moving"}]}
        ]}""")
                .toHome()
        assertEquals(3, manifest.size)
        assertEquals(HomeBlock.Greeting("Hello"), manifest[0])
        assertTrue(manifest[1] is HomeBlock.Promotion)
        val tile = (manifest[2] as HomeBlock.Tiles).items.single()
        assertNull(tile.subtitle)
        assertNull(tile.action)
    }

    @Test
    fun `tile blocks keep their server kind`() {
        val kinds =
            json
                .decodeFromString<ManifestDto>(
                    """{"blocks":[
            {"type":"classCarousel","title":"Your week"},
            {"type":"myRewards","title":"Rewards"},
            {"type":"myGoals","title":"Goals"}
        ]}""")
                .toHome()
                .map { (it as HomeBlock.Tiles).kind }
        assertEquals(listOf(TileKind.CLASSES, TileKind.REWARDS, TileKind.GOALS), kinds)
    }

    @Test
    fun `malformed and duplicate classes are dropped without losing the day`() {
        fun item(id: String, startsAt: String) =
            """{"classId":"$id","clubId":"club","title":"Yoga","startsAt":"$startsAt","endsAt":"2026-11-13T09:00:00Z","timezone":"Europe/London","spots":20,"available":5,"status":"available","userBookingStatus":"none"}"""
        val day =
            json
                .decodeFromString<TimetableDto>(
                    """{"clubId":"club","weekStart":"2026-11-09","weekEnd":"2026-11-15","selectedDate":"2026-11-13","days":[{"date":"2026-11-13","classes":[
            ${item("late", "2026-11-13T08:00:00Z")},
            ${item("broken", "tomorrow morning")},
            ${item("early", "2026-11-13T07:00:00Z")},
            ${item("early", "2026-11-13T07:00:00Z")}
        ]}]}""")
                .toTimetable()
                .days
                .single()
        assertEquals(listOf("early", "late"), day.classes.map { it.ref.classId })
    }

    @Test
    fun `days with malformed dates are dropped and dates are typed`() {
        val table =
            json
                .decodeFromString<TimetableDto>(
                    """{"clubId":"club","weekStart":"2026-11-09","weekEnd":"2026-11-15","selectedDate":"2026-11-13","days":[
            {"date":"2026-11-13","classes":[]},
            {"date":"Friday","classes":[]}
        ]}""")
                .toTimetable()
        assertEquals(java.time.LocalDate.of(2026, 11, 13), table.selectedDate)
        assertEquals(listOf(java.time.LocalDate.of(2026, 11, 13)), table.days.map { it.date })
    }

    @Test
    fun `unknown server actions remain inactive`() {
        assertNull(
            json
                .decodeFromString<HomeActionDto>(
                    """{"type":"openExternalUrl","url":"https://example.com"}""")
                .toAction())
        assertNull(
            json
                .decodeFromString<HomeActionDto>("""{"type":"openClass","clubId":"club"}""")
                .toAction())
    }

    @Test
    fun `unknown booking status is never treated as unbooked`() {
        assertEquals(BookingStatus.UNKNOWN, bookingStatus("reserved"))
        assertEquals(BookingStatus.UNKNOWN, bookingStatus(null))
    }
}

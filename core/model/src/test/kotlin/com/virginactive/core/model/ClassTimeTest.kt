package com.virginactive.core.model

import java.time.*
import java.util.TimeZone
import org.junit.Assert.*
import org.junit.Test

class ClassTimeTest {
    private fun fixture() =
        FitnessClass(
            ClassRef("club", "class"),
            "Yoga",
            "Trainer",
            "yoga",
            OffsetDateTime.parse("2026-11-13T07:00:00+02:00"),
            OffsetDateTime.parse("2026-11-13T08:00:00+02:00"),
            "Africa/Johannesburg",
            20,
            0,
            Availability.FULL,
            BookingStatus.BOOKED,
            0)

    @Test
    fun `venue time keeps its offset regardless of device timezone`() {
        val previous = TimeZone.getDefault()
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"))
            assertEquals(Instant.parse("2026-11-13T05:00:00Z"), fixture().startsAt.toInstant())
        } finally {
            TimeZone.setDefault(previous)
        }
    }

    @Test
    fun `late cancellation warning has precise twelve hour boundary`() {
        val fitnessClass = fixture()
        val start = fitnessClass.startsAt.toInstant()
        assertFalse(fitnessClass.cancellationMayForfeitCost(start.minusSeconds(12 * 3600)))
        assertTrue(fitnessClass.cancellationMayForfeitCost(start.minusSeconds(12 * 3600 - 1)))
        assertFalse(fitnessClass.cancellationMayForfeitCost(start.plusSeconds(1)))
        assertTrue(fitnessClass.hasStarted(start))
    }
}

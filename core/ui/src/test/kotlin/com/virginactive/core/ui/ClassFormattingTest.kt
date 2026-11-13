package com.virginactive.core.ui

import com.virginactive.core.model.*
import java.time.OffsetDateTime
import java.util.Locale
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class ClassFormattingTest {
    private val yoga =
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
    fun `labels show venue time even when the device is elsewhere`() {
        val zone = TimeZone.getDefault()
        val locale = Locale.getDefault()
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"))
            Locale.setDefault(Locale.UK)
            assertEquals("07:00 – 08:00", yoga.timeLabel)
            assertEquals("Fri, 13 Nov", yoga.dateLabel)
        } finally {
            TimeZone.setDefault(zone)
            Locale.setDefault(locale)
        }
    }
}

package com.virginactive.core.ui

import com.virginactive.core.model.*
import java.time.Instant
import java.time.OffsetDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class ClassStatusTest {
    private val before = Instant.parse("2026-11-13T04:00:00Z")
    private val during = Instant.parse("2026-11-13T05:30:00Z")
    private val after = Instant.parse("2026-11-13T07:00:00Z")
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
            5,
            Availability.AVAILABLE,
            BookingStatus.NONE,
            0)

    private fun label(fitnessClass: FitnessClass, now: Instant = before) =
        fitnessClass.status(now).label

    @Test
    fun `upcoming classes show booking state and availability`() {
        assertEquals("5 spots left", label(yoga))
        assertEquals("1 spot left", label(yoga.copy(available = 1)))
        assertEquals("Booked", label(yoga.copy(bookingStatus = BookingStatus.BOOKED)))
        assertEquals("Waitlisted", label(yoga.copy(bookingStatus = BookingStatus.WAITLISTED)))
        assertEquals(
            "Full · waitlist open",
            label(yoga.copy(available = 0, availability = Availability.FULL)))
        assertEquals("Unavailable", label(yoga.copy(availability = Availability.UNKNOWN)))
        assertEquals("Unavailable", label(yoga.copy(bookingStatus = BookingStatus.UNKNOWN)))
    }

    @Test
    fun `time and cancellation take priority over booking state`() {
        val booked = yoga.copy(bookingStatus = BookingStatus.BOOKED)
        assertEquals("In progress", label(booked, during))
        assertEquals("Finished", label(booked, after))
        assertEquals("Cancelled", label(booked.copy(availability = Availability.CANCELLED), after))
    }
}
